/* =======================================================
* ypSimpleScroll
* 3/11/2001
* 
* http://www.youngpup.net/
* ======================================================= */

// Modified by Sergi Meseguer (www.zigotica.com) 04/2004
// Now it works with dragger and can use multiple instances in a page



ypSimpleScroll.prototype.scrollNorth = function(count) { this.startScroll(90, count) }
ypSimpleScroll.prototype.scrollSouth = function(count) { this.startScroll(270, count) }
ypSimpleScroll.prototype.scrollWest = function(count) { this.startScroll(180, count) }
ypSimpleScroll.prototype.scrollEast = function(count) { this.startScroll(0, count) }

ypSimpleScroll.prototype.startScroll = function(deg, count) {
	if (this.loaded){
		if (this.aniTimer) window.clearTimeout(this.aniTimer)
		this.overrideScrollAngle(deg)
		this.speed = this.origSpeed
		this.lastTime = (new Date()).getTime() - this.y.minRes
		this.aniTimer = window.setTimeout(this.gRef + ".scroll('"+deg+"','"+count+"')", this.y.minRes)
	}
}

ypSimpleScroll.prototype.endScroll = function() {
	if (this.loaded){
		window.clearTimeout(this.aniTimer)
		this.aniTimer = 0;
		this.speed = this.origSpeed
	}
}

ypSimpleScroll.prototype.overrideScrollAngle = function(deg) {
	if (this.loaded){
		deg = deg % 360
		if (deg % 90 == 0) {
			var cos = deg == 0 ? 1 : deg == 180 ? -1 : 0
			var sin = deg == 90 ? -1 : deg == 270 ? 1 : 0
		} 
		else {
			var angle = deg * Math.PI / 180
			var cos = Math.cos(angle)
			var sin = Math.sin(angle)
			sin = -sin
		}
		this.fx = cos / (Math.abs(cos) + Math.abs(sin))
		this.fy = sin / (Math.abs(cos) + Math.abs(sin))
		this.stopH = deg == 90 || deg == 270 ? this.scrollLeft : deg < 90 || deg > 270 ? this.scrollW : 0
		this.stopV = deg == 0 || deg == 180 ? this.scrollTop : deg < 180 ? 0 : this.scrollH
	}
}

ypSimpleScroll.prototype.overrideScrollSpeed = function(speed) {
	if (this.loaded) this.speed = speed
}


ypSimpleScroll.prototype.scrollTo = function(stopH, stopV, aniLen) {
	if (this.loaded){
		if (stopH != this.scrollLeft || stopV != this.scrollTop) {
			if (this.aniTimer) window.clearTimeout(this.aniTimer)
			this.lastTime = (new Date()).getTime()
			var dx = Math.abs(stopH - this.scrollLeft)
			var dy = Math.abs(stopV - this.scrollTop)
			var d = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2))
			this.fx = (stopH - this.scrollLeft) / (dx + dy)
			this.fy = (stopV - this.scrollTop) / (dx + dy)
			this.stopH = stopH
			this.stopV = stopV
			this.speed = d / aniLen * 1000
			window.setTimeout(this.gRef + ".scroll()", this.y.minRes)
		}
	}
}

ypSimpleScroll.prototype.jumpTo = function(nx, ny) { 
	if (this.loaded){
		nx = Math.min(Math.max(nx, 0), this.scrollW)
		ny = Math.min(Math.max(ny, 0), this.scrollH)
		this.scrollLeft = nx
		this.scrollTop = ny
		if (this.y.ns4)this.content.moveTo(-nx, -ny)
		else {
			this.content.style.left = -nx + "px"
			this.content.style.top = -ny + "px"
		}
	}
}

ypSimpleScroll.minRes = 10
ypSimpleScroll.ie = document.all ? 1 : 0
ypSimpleScroll.ns4 = document.layers ? 1 : 0
ypSimpleScroll.dom = document.getElementById ? 1 : 0
ypSimpleScroll.mac = navigator.platform == "MacPPC"
ypSimpleScroll.mo5 = document.getElementById && !document.all ? 1 : 0

ypSimpleScroll.prototype.scroll = function(deg,count) {
	this.aniTimer = window.setTimeout(this.gRef + ".scroll('"+deg+"','"+count+"')", this.y.minRes)
	var nt = (new Date()).getTime()
	var d = Math.round((nt - this.lastTime) / 1000 * this.speed)
	if (d > 0){
		var nx = d * this.fx + this.scrollLeft
		var ny = d * this.fy + this.scrollTop
		var xOut = (nx >= this.scrollLeft && nx >= this.stopH) || (nx <= this.scrollLeft && nx <= this.stopH)
		var yOut = (ny >= this.scrollTop && ny >= this.stopV) || (ny <= this.scrollTop && ny <= this.stopV)
		if (nt - this.lastTime != 0 && 
			((this.fx == 0 && this.fy == 0) || 
			(this.fy == 0 && xOut) || 
			(this.fx == 0 && yOut) || 
			(this.fx != 0 && this.fy != 0 && 
			xOut && yOut))) {
			this.jumpTo(this.stopH, this.stopV)
			this.endScroll()
		}
		else {
			this.jumpTo(nx, ny)
			this.lastTime = nt
		}
	// (zgtc) now we also update dragger position:
	if(deg=='270')	theThumb[count].style.top = parseInt(((theThumb[count].maxY-theThumb[count].minY)*this.scrollTop/this.stopV)+theThumb[count].minY) + "px"; //ok nomes down
	if(deg=='90')	theThumb[count].style.top = parseInt(((theThumb[count].maxY-theThumb[count].minY)*this.scrollTop/this.scrollH)+theThumb[count].minY) + "px"; //ok nomes down
	}
}

function ypSimpleScroll(id, left, top, width, height, speed) {
	var y = this.y = ypSimpleScroll
	if (document.layers && !y.ns4) history.go(0)
	if (y.ie || y.ns4 || y.dom) {
		this.loaded = false
		this.id = id
		this.origSpeed = speed
		this.aniTimer = false
		this.op = ""
		this.lastTime = 0
		this.clipH = height
		this.clipW = width
		this.scrollTop = 0
		this.scrollLeft = 0
		this.gRef = "ypSimpleScroll_"+id
		eval(this.gRef+"=this")
		var d = document
		d.write('<style type="text/css">')
		d.write('#' + this.id + 'Container { left:' + left + 'px; top:' + top + 'px; width:' + width + 'px; height:' + height + 'px; clip:rect(0 ' + width + ' ' + height + ' 0); overflow:hidden; }')
		d.write('#' + this.id + 'Container, #' + this.id + 'Content { position:absolute; }')
		d.write('#' + this.id + 'Content { left:' + (-this.scrollLeft) + 'px; top:' + (-this.scrollTop) + 'px; width:' + width + 'px; }')
		// (zgtc) fix to overwrite p/div/ul width (would be clipped if wider than scroller in css):
		d.write('#' + this.id + 'Container p, #' + this.id + 'Container div {width:' + parseInt(width-10) + 'px; }')
		d.write('</style>')
	}
}

ypSimpleScroll.prototype.load = function() {
	var d, lyrId1, lyrId2
	d = document
	lyrId1 = this.id + "Container"
	lyrId2 = this.id + "Content"
	this.container = this.y.dom ? d.getElementById(lyrId1) : this.y.ie ? d.all[lyrId1] : d.layers[lyrId1]
	this.content = obj2 = this.y.ns4 ? this.container.layers[lyrId2] : this.y.ie ? d.all[lyrId2] : d.getElementById(lyrId2)
	this.docH = Math.max(this.y.ns4 ? this.content.document.height : this.content.offsetHeight, this.clipH)
	this.docW = Math.max(this.y.ns4 ? this.content.document.width : this.content.offsetWidth, this.clipW)
	this.scrollH = this.docH - this.clipH
	this.scrollW = this.docW - this.clipW
	this.loaded = true
	this.scrollLeft = Math.max(Math.min(this.scrollLeft, this.scrollW),0)
	this.scrollTop = Math.max(Math.min(this.scrollTop, this.scrollH),0)
	this.jumpTo(this.scrollLeft, this.scrollTop)
}