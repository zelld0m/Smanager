(function($) {

	var Term = function(options) {
		var base = this;

		base.options = $.extend({}, options);
		delete options['term'];
		base.$container = $("<span class=\"sorter\">");

		base.$term = $("<span class=\"term\">");
		base.$term.on({
			keydown : function(e) {
				switch (e.keyCode) {
				case 9: // tab
					e.preventDefault();
					if (e.shiftKey && base.$container.prev().length > 0) {
						base.$container.prev().find(":first").click();
						break;
					}

					if (!e.shiftKey && base.$term.text().trim()
							&& base.$container.next().length > 0) {
						$(base.$container.next()).find(":first").click();
						break;
					}
				case 13: // enter
					e.preventDefault();
					if (base.$term.text().trim()
							&& base.$container.find(":first").next().length == 0) {
						new Term(options);
					} else {
						base.$term.blur();
					}
				}
			},

			blur : function(e) {
				var val = base.$term.text();
				base.$term.removeClass("error");
				base.$container.removeClass("edit-mode");

				if (!val) {
					base.$container.remove();
				}
				base.options.container.trigger("change");
			}
		}).editable({editEnabled: function() {
			if (base.options.rule.editable) {
				base.$container.addClass("edit-mode");
			}

			return base.options.rule.editable;
		}});

		base.$container.append(base.$term);
		base.options.container.append(base.$container);

		base.$term.text(base.options.term);
		
		if (!base.options.term) base.$term.click();
	};

	var SpellRule = function(data) {
		var base = this;

		base._data = data;
		base.$el = base.$spellRuleTemplate.clone().removeAttr("id");
		base.$searchTerms = base.$el.find("#searchTerms");
		base.$suggestions = base.$el.find("#suggestions");

		// When spell rule is created with data provided
		if (data) {
			base.id = data.ruleId;
			base.$el.attr("id", data.ruleId);
			base.originalSearchTerms = data.searchTerms;
			base.originalSuggestions = data.suggestions;
			base.editable = false;
			
			// render search terms and suggestions
			base.$searchTerms.text("");
			$.each(data.searchTerms, function(idx) {
				new Term({term: data.searchTerms[idx], rule: base, container: base.$searchTerms});
			});

			base.$suggestions.text("");
			$.each(data.suggestions, function(idx) {
				new Term({term: data.suggestions[idx], rule: base, container: base.$suggestions});
			});
		} else {
			base.id = null;
			base.originalSearchTerms = [];
			base.originalSuggestions = [];
		}

		base.$el.data('spellRule', base);
		base.$searchTerms.on({
			click: function(e) {
				if (base.editable) {
					if (base.$searchTerms.find(".term").length == 0) {
						base.$searchTerms.text("");
					}
					new Term({rule: base, container: base.$searchTerms });
				}
			},
			change: function() {
				if (base.$searchTerms.find(".term").length == 0) {
					base.$searchTerms.html(DidYouMean.messages.addNewTerm);
				}
			}
		});
		base.$suggestions.on({
			click: function(e) {
				if (base.editable) {
					if (base.$suggestions.find(".term").length == 0) {
						base.$suggestions.text("");
					}
					new Term({rule: base, container: base.$suggestions });
				}
			},
			change: function() {
				if (base.$suggestions.find(".term").length == 0) {
					base.$suggestions.html(DidYouMean.messages.addNewTerm);
				}
			}
		});

		if (DidYouMean.mode === 'add') {
			DidYouMean.$footer.before(base.$el);
		} else {
			DidYouMean.$table.append(base.$el);
		}

		base.$tooltip = base.$iconsTemplate.clone().cutebar({
			container : base.$el,
			groups : {
				'editing'        : [ 'undo-link'   ],
				'editing-adding' : [ 'delete-link' ]
			},
			events : {
				'delete-link' : function() {
					if (base.id) {
						DidYouMean.deleted.push({el : base.$el, previous: base.$el.prev()});
						base.$el.detach();
					} else {
						base.$el.remove();
					}
				},
				'undo-link' : function() {
					base.$searchTerms.text("");
					base.$suggestions.text("");
	
					$.each(base.originalSearchTerms, function() {
						new Term({
							container : base.$searchTerms,
							term : this.toString(),
							rule : base
						});
					});
	
					$.each(base.originalSuggestions, function() {
						new Term({
							container : base.$suggestions,
							term : this.toString(),
							rule : base
						});
					});
				}
			},
			qtip : $.extend({}, $.cutebar.defaultOptions.qtip, {
				events : {
					show: function() {
						return DidYouMean.mode == 'edit' || DidYouMean.mode == 'add';
					}
				}
			})
		});

		base.$el.show();

		if (!data) {
			base.$searchTerms.html(DidYouMean.messages.addNewTerm);
			base.$suggestions.html(DidYouMean.messages.addNewTerm);
		}
	};

	SpellRule.prototype = {
		data : function() {
			var self = this;
			
			if (!self._data) {
				self._data = {
						ruleId : self.id,
						searchTerms : self.$searchTerms.find(".term").map(function() { return $(this).text(); }).get(),
						suggestions : self.$suggestions.find(".term").map(function() { return $(this).text(); }).get()
					};
			} else {
				self._data.searchTerms = self.$searchTerms.find(".term").map(function() { return $(this).text(); }).get();
				self._data.suggestions = self.$suggestions.find(".term").map(function() { return $(this).text(); }).get();
			}

			return self._data;
		},

		resetTooltip : function() {
			if (DidYouMean.mode == 'add') {
				this.$tooltip.cutebar('hideGroup', ['editing']);
				this.$tooltip.cutebar('showGroup', ['editing-adding']);
			} else if (DidYouMean.mode == 'edit') {
				this.$tooltip.cutebar('showGroup', ['editing']);
				this.$tooltip.cutebar('showGroup', ['editing-adding']);
			}
			
		},

		highlight : function(data) {
			var self = this;
			$.each(data, function(){
				var duplicateTerm = this.toString();

				self.$searchTerms.find(".term:contains(" + duplicateTerm + ")").each(function() {
					if ($(this).text() == duplicateTerm) {
						$(this).addClass("error");
					}
				});
			});
		},

		setEditable : function(editable) {
			var self = this;
			
			self.$el.attr("sr-editable", editable);
			self.editable = editable;
			self.$suggestions.sortable({cancel: 'span.term'}).sortable(editable ? 'enable' : 'disable');
			self.resetTooltip();
		},
		
		revert : function() {
			var self = this;
			
			self.$searchTerms.text("");
			self.$suggestions.text("");

			$.each(self.originalSearchTerms, function() {
				new Term({
					container : self.$searchTerms,
					term : this.toString(),
					rule : self
				});
			});

			$.each(self.originalSuggestions, function() {
				new Term({
					container : self.$suggestions,
					term : this.toString(),
					rule : self
				});
			});
		},
		
		isModified : function() {
			var self = this;
			var data = self.data();

			if (self.originalSearchTerms.length != data.searchTerms.length
					|| self.originalSuggestions.length != data.suggestions.length) {
				return true;
			}

			for (var i = 0; i < self.originalSearchTerms.length; i++) {
				if (data.searchTerms[i] != self.originalSearchTerms[i]) {
					return true;
				}
			}

			for (var i = 0; i < self.originalSuggestions.length; i++) {
				if (data.suggestions[i] != self.originalSuggestions[i]) {
					return true;
				}
			}

			return false;
		}
	};

	var DidYouMean = {
		// filters
		searchTerm : null,
		suggestion : null,
		status : null,
		
		// deleted rules
		deleted : [],

		// editing mode (display, add, edit)
		mode: 'display',

		// page size
		PAGE_SIZE : 10,
		currentPage: 1,
		
		// default ref id for did you mean
		RULE_TYPE : 'Did You Mean',
		rule: {ruleId: 'spell_rule', ruleName: ""},

		initButtons: function() {
			var self = this;

			// action buttons
			self.$addButton = $("#add-button");
			self.$editButton = $("#edit-button");
			self.$saveButton = $("#save-button");
			self.$cancelButton = $("#cancel-button");

			if (!self.ruleStatus.locked) {
				self.$addButton.on({
					click : function(e) {
						$(".button-group-0").hide();
						$(".button-group-1").show();
						self.mode = 'add';

						self.$rows = self.$table.find("tr:not(#header)").detach();
						self.$table.append(self.$footer);
						self.$footer.show();
						self.$pager.hide();
						self.$clearButton.off();
						self.$filterButton.off();
						self.$maxSuggest.parent().hide();
					}
				});

				self.$editButton.on({
					click : function(e) {
						$(".button-group-0").hide();
						$(".button-group-1").show();
						self.mode = 'edit';
						self.$maxSuggest.removeAttr("disabled");
						self.$pager.hide();
						self.$clearButton.off();
						self.$filterButton.off();

						var rows = self.$table.find("tr:not(#header)");

						for ( var i = 0; i < rows.length; i++) {
							$(rows[i]).data('spellRule').setEditable(true);
						}
					}
				});

				self.$cancelButton.on({
					click : function(e) {
						$(".button-group-1").hide();
						$(".button-group-0").show();

						if (self.mode == 'add') {
							self.$footer.detach();
							self.$table.find("tr:not(#header)").remove();
							self.$table.append(self.$rows);
							self.$maxSuggest.parent().show();
						} else if (self.mode == 'edit') {
							for (var i = self.deleted.length - 1; i >= 0; i--) {
								$(self.deleted[i].previous).after(self.deleted[i].el);
							}

							self.deleted = [];

							var rows = self.$table.find("tr:not(#header)");

							for ( var i = 0; i < rows.length; i++) {
								$(rows[i]).data('spellRule').revert();
								$(rows[i]).data('spellRule').setEditable(false);
							}
							
							self.$maxSuggest.attr("disabled", "true");
							self.$maxSuggest.val(self.maxSuggest);
						}

						self.$pager.show();
						self.mode = 'display';
						self.addFilterEventHandlers();
					}
				});

				self.$saveButton.on({
					click : function(e) {
						if (self.mode == 'add') {
							var rules = self.$table.find("tr.spell-rule");
							var entities = [];

							for ( var i = 0; i < rules.length; i++) {
								entities.push($(rules[i]).data('spellRule').data());
							}

							var hasChanges = entities.length > 0;

							if (self.validate(entities) && hasChanges) {
								SpellRuleServiceJS.addSpellRuleBatch(entities,
									function(response) {
										// success
										if (response.status == 0) {
											self.$footer.detach();
											self.$table.find("tr:not(#header)").remove();
											self.mode = 'display';
											self.handlePageLink(1);
											$(".button-group-1").hide();
											$(".button-group-0").show();
											self.$pager.show();
											self.$maxSuggest.parent().show();
											self.addFilterEventHandlers();
										} else {
											jAlert(response.errorMessage.message);
											
											if (response.errorMessage.data) {
												for ( var i = 0; i < rules.length; i++) {
													$(rules[i]).data('spellRule').highlight(response.errorMessage.data);
												}
											}
										}
									});
							} else if (!hasChanges) {
								self.$cancelButton.click();
							}
						} else if (self.mode == 'edit') {
							var rules = self.$table.find("tr.spell-rule");
							var entities = [];
							var deleted = [];

							for ( var i = 0; i < rules.length; i++) {
								var spellRuleData = $(rules[i]).data('spellRule');
								if (spellRuleData.isModified()) {
									entities.push(spellRuleData.data());
								}
							}

							for ( var i = 0; i < self.deleted.length; i++) {
								deleted.push($(self.deleted[i].el).data('spellRule').data());
							}

							var hasChanges = self.$maxSuggest.val() != self.maxSuggest || entities.length > 0 || deleted.length > 0;
							
							if (self.validate(entities) && hasChanges) {
								SpellRuleServiceJS.updateSpellRuleBatch(self.$maxSuggest.val(), entities, deleted,
									function(response) {
										// success
										if (response.status == 0) {
											self.$table.find("tr:not(#header)").remove();
											self.mode = 'display';
											self.handlePageLink();
											self.maxSuggest = self.$maxSuggest.val();
											$(".button-group-1").hide();
											$(".button-group-0").show();
											self.$maxSuggest.attr("disabled", true);
											self.$pager.show();
											self.addFilterEventHandlers();
										} else {
											jAlert(response.errorMessage.message);
											
											if (response.errorMessage.data) {
												for ( var i = 0; i < rules.length; i++) {
													$(rules[i]).data('spellRule').highlight(response.errorMessage.data);
												}
											}
										}
									});
							} else if (!hasChanges){
								self.$cancelButton.click();
							}
						}
					}
				});

				self.$addButton.show();
				self.$editButton.show();
				$("#spell-rules #action-buttons").show();
			} else {
				$("#spell-rules #action-buttons").hide();
			}
		},
		
		validate: function(entities, maxSuggest) {
			for (var i = 0; i < entities.length; i++) {
				if (entities[i].searchTerms.length == 0 || entities[i].suggestions.length == 0) {
					jAlert(DidYouMean.messages.requiredFields);
					return false;
				}
			}
			
			return true;
		},

		initTable: function() {
			var self = this;

			// table
			self.$table = $("#spell-table");
			self.$footer = self.$table.find("#spell-table-footer").detach();
			self.$itemTemplate = self.$table.find("#itemTemplate");
			self.$noResultsRow = self.$table.find("#noResultsFound").detach();
			self.$pager = $("#topPaging, #bottomPaging");

			self.$footer.on({
				click : function() {
					new SpellRule().setEditable(true);
				}
			});

			SpellRuleServiceJS.getMaxSuggest(function(response) {
				if (response.status == 0) {
					self.$maxSuggest.val(response.data);
					self.maxSuggest = response.data;
					self.handlePageLink(1);
				} else {
					jAlert(response.errorMessage.message);
				}
			});
		},
		
		initFilters: function() {
			var self = this;

			// search filters
			self.$searchTermFilter = $("#searchTerm-filter");
			self.$suggestionFilter = $("#suggestion-filter");
			self.$statusFilter = $("#status-filter");
			self.$filterButton = $("#filter-button");
			self.$clearButton = $("#clear-button");

			self.addFilterEventHandlers();
		},

		addFilterEventHandlers: function() {
			var self = this;

			self.$filterButton.off().on({click: function(e) {
				self.filter();
			}});

			self.$clearButton.off().on({click: function(e) {
				self.$searchTermFilter.val("");
				self.$suggestionFilter.val("");
				self.$statusFilter.val("");
				self.filter();
			}});
		},

		filter:  function(e) {
			var self = this;

			self.searchTerm = self.$searchTermFilter.val();
			self.suggestion = self.$suggestionFilter.val();
			self.status = self.$statusFilter.val();

			self.handlePageLink(1);
		},

		init : function() {
			var self = this;

			self.$maxSuggest = $("#max-suggest");

			$("#ruleStatus").rulestatus({
				moduleName: self.RULE_TYPE,
				ruleType: self.RULE_TYPE,
				rule: self.rule,
				enableVersion: true,
				authorizeRuleBackup: true,
				authorizeSubmitForApproval: true,
				deleteVersionsPhysically: true,
				enableCompare: false,
				enableSingleVersionDownload: true,
				postRestoreCallback: function(base, rule){
					jAlert("Did you mean rules restored.", "Version Restored", function() {
						location.reload();
					});
				},
				afterSubmitForApprovalRequest:function(ruleStatus){
					self.selectedRuleStatus = ruleStatus;
					self.init();
				},
				beforeRuleStatusRequest: function(){
				},
				afterRuleStatusRequest: function(ruleStatus){
					$("#ruleStatus").show();
					$("#spell-rules").show();
					$("#preloader").hide();
					self.ruleStatus = ruleStatus;
					self.initButtons();
					self.initTable();
					self.initFilters();
				}
			});
		},

		handleSearchResponse : function(response, page) {
			var self = this;

			if (response.status == 0) {
				this.$table.find("tr:not(#header)").remove();

				if (response.data.list.length > 0) {
					self.currentPage = page;
					for (var i = 0; i < response.data.list.length; i++) {
						new SpellRule(response.data.list[i]);
					}
					self.$pager.paginate(self.getPageOptions(page, response.data.totalSize));
				} else {
					this.$table.append(this.$noResultsRow.clone().show());
					self.$pager.hide();
				}
			} else {
				jAlert(response.errorMessage.data);
			}
		},

		handlePageLink : function(page) {
			var self = this;
			var nextPage = page || self.currentPage;

			SpellRuleServiceJS.getSpellRule(null, self.searchTerm, self.suggestion, self.status, nextPage, self.PAGE_SIZE,
				function(response) {
					self.handleSearchResponse(response, nextPage);
				});
		},

		getPageOptions : function(currentPage, totalSize) {
			var self = this;
			var options = {
				totalItem : totalSize,
				currentPage : currentPage,
				pageSize : self.PAGE_SIZE,
				pageLinkCallback : function(e) {
					self.handlePageLink(e.data.page);
				},
				firstLinkCallback : function(e) {
					self.handlePageLink(1);
				},
				nextLinkCallback : function(e) {
					self.handlePageLink(e.data.page + 1);
				},
				prevLinkCallback : function(e) {
					self.handlePageLink(e.data.page - 1);
				},
				lastLinkCallback : function(e) {
					self.handlePageLink(Math.ceil(self.totalSize / self.itemsPerPage));
				}
			};
			
			return options;
		},

		messages : {
			addNewTerm : "<span style=\"color: #CCC\">Click to add new term</span>",
			requiredFields : "Search Terms and Suggestions are required.",
			noDataFound : "No data found."
		}
	};

	$(document).ready(function() {
		SpellRule.prototype.$iconsTemplate = $("#templates .icons").detach();
		SpellRule.prototype.$spellRuleTemplate = $("#spell-table #itemTemplate").detach();
		DidYouMean.init();
	});
})(jQuery);