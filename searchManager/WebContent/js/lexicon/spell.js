(function($){
	
	var Messages = {
		addNewTerm: "<Click to add new term>"
	};

	var Term = function(data) {
		var $container = $("<span class=\"term\">");
		var $inputEl = $("<input type=\"text\" class=\"term-input\">");
		var $displayEl = $("<span class=\"term-input-display\">");

		$inputEl.on({
			click : function(e) {
				return false;
			},

			keydown: function(e) {
				switch (e.keyCode) {
					case 27:
						$inputEl.val($displayEl.text());
						$inputEl.blur();
						break;
					case 9:
						e.preventDefault();
						if (e.shiftKey && $container.prev().length > 0) {
							$container.prev().click();
							break;
						}

						if (!e.shiftKey && $inputEl.val().trim() && $container.next().length > 0) {
							$($container.next()).click();
							break;
						}
					case 13:
						if ($inputEl.val().trim() && $container.next().length == 0) {
							new Term(data);
						} else {
							$inputEl.blur();
						}
				}
			},
			
			focus: function(e) {
				$inputEl.select();
			},

			blur: function(e) {
				var val = $inputEl.val().trim();

				if (val) {
					$displayEl.text(val);
					$inputEl.val(val);
					$inputEl.hide();
					$displayEl.show();
				} else {
					var idx = 0;

					while (idx < data.collection.length) {
						if (data.collection[idx] == object) {
							break;
						}

						idx++;
					}

					if (idx < data.collection.length) {
						data.collection.splice(idx, 1);
					}

					if (!data.rule.hasContents()) {
						data.rule.destroy();
					} else {
						$container.remove();
	
						if (data.collection.length == 0) {
							data.container.text(Messages.addNewTerm);
						}
					}
				}
			}
		});

		$container.on({
			click: function(e) {
				if (data.rule.editable) {
					$displayEl.hide();
					$inputEl.show();
					$inputEl.focus();
				}
				return false;
			}
		});

		$container.append($displayEl).append($inputEl);

		var object = {
			$el : $container,
			focus: function() {
				if (data.rule.editable) {
					$displayEl.hide();
					$inputEl.show();
					$inputEl.focus();
				}
			},
		};

		data.collection.push(object);
		data.container.append($container);

		if (data.term) {
			$inputEl.val(data.term.$el.text()).hide();
			$displayEl.text(data.term.$el.text());
			data.term = null;
		} else {
		    object.focus();
		}
		
		return object;
	};

	var SpellRule = function(parent) {
		var self = this;

		var $el = $("#spell-table #itemTemplate").clone().removeAttr("id");
		var $misspellings = $el.find(".misspell-list");
		var $suggestions = $el.find(".suggest-list");
		var origMisspelledTerms = [];
		var origSuggestedTerms = [];
		var misspelledTerms = [];
		var suggestedTerms = [];

		var afterEdit = function() {
			if (misspelledTerms.length == 0) {
				$misspellings.text("");
			}
			if (suggestedTerms.length == 0) {
				$suggestions.text("");
			}
		};

		var beforeEdit = function() {
			if (misspelledTerms.length == 0) {
				$misspellings.text(Messages.addNewTerm);
			}
			if (suggestedTerms.length == 0) {
				$suggestions.text(Messages.addNewTerm);
			}
		};

		var obj = {
			id: null,
			init: function() {
				var _self = this;
				$("#spell-table #itemTemplate").before($el);
				self.addTerm({data: {rule: _self, collection: misspelledTerms, container: $misspellings}});
			},
			hasContents: function() {
				return misspelledTerms.length > 0 || suggestedTerms.length > 0;
			},
			destroy: function() {
				$el.remove();
			},
			editable: true
		};

		var $tooltip = $el.find(".icons").cutebar({
			container : $el,
			groups : {
				'locked' : [ 'edit-locked' ],
				'not-editing' : [ 'edit-link', 'delete-link' ],
				'editing' : [ 'save-link', 'cancel-link' ]
			},
			events : {
				'edit-link' : function() {
					$tooltip.cutebar('hideGroup', 'not-editing');
					$tooltip.cutebar('showGroup', 'editing');
					obj.editable = true;
					beforeEdit();
				},
				'delete-link' : function() {
					obj.destroy();
				},
				'save-link' : function() {
					Test.save(obj, function() {
						$tooltip.cutebar('hideGroup', 'editing');
						$tooltip.cutebar('showGroup', 'not-editing');
						obj.editable = false;

						origMisspelledTerms = misspelledTerms.slice();
						origSuggestedTerms = suggestedTerms.slice();
						afterEdit();
					});
				},
				'cancel-link' : function() {
					if (obj.id) {
						$tooltip.cutebar('hideGroup', 'editing');
						$tooltip.cutebar('showGroup', 'not-editing');
						obj.editable = false;

						misspelledTerms.splice(0);
						suggestedTerms.splice(0);
						$misspellings.text("");
						$suggestions.text("");

						$.each(origMisspelledTerms, function(idx) {
							new Term({
								collection : misspelledTerms,
								container : $misspellings,
								term : origMisspelledTerms[idx],
								rule : obj
							});
						});

						$.each(origSuggestedTerms, function(idx) {
							new Term({
								collection : suggestedTerms,
								container : $suggestions,
								term : origSuggestedTerms[idx],
								rule : obj
							});
						});
						
						afterEdit();
					} else {
						obj.destroy();
					}
				}
			},
			qtip : $.extend({}, $.cutebar.defaultOptions.qtip, {
				events : {
					hide: function() {
						return !obj.editable;
					}
				}
			})
		});

		$misspellings.text(Messages.addNewTerm).parent().on({click: this.addTerm}, {rule: obj, collection: misspelledTerms, container: $misspellings});
		$suggestions.text(Messages.addNewTerm).parent().on({click: this.addTerm}, {rule: obj, collection: suggestedTerms, container: $suggestions});
		$tooltip.cutebar("showGroup", "editing");
		$tooltip.cutebar("hideGroup", "not-editing");
		$tooltip.cutebar("hideGroup", "locked");
		$el.show();

		return obj;
	};

	SpellRule.prototype = {
		addTerm : function(e) {
			if (e.data.rule.editable) {
				if (e.data.collection.length == 0) {
					e.data.container.text("");
				}

				new Term(e.data);
			}
		}
	};
	
	var Test = {
			charset: "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-",
	
			generateId: function() {
				var id = "";
				
				for (var i = 0; i < 20; i++) {
					id += this.charset.charAt(Math.floor(Math.random() * this.charset.length));
				}
				
				return id;
			},

			save: function(rule, callback) {
				if (!rule.id) {
				    rule.id = this.generateId();
				}

				if (typeof(callback) == 'function') {
					callback();
				}
			}
	};

	var DidYouMean = {
		$table: null,
		$itemTemplate: null,
		$footer: null,

		init: function(){
			var self= this;

			this.$table = $("#spell-table");
			this.$footer = this.$table.find("#spell-table-footer");
			this.$itemTemplate = $("#spell-table #itemTemplate");
			this.$footer.on({click: function() {
				var rule = new SpellRule();
				rule.init();
			}});
		}
	};

    $(document).ready(function() {
	    DidYouMean.init();
	});
})(jQuery);