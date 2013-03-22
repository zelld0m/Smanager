package com.search.manager.solr.constants;

public class Constants {

	public static final String ECOST = "ecost";
	public static final String SBN = "sbn";
	public static final String PCMALLCAP = "pcmallcap";
	public static final String PCMALL = "pcmall";
	public static final String MACMALL = "macmall";
	public static final String PCMGBD = "pcmgbd";
	public static final String MACMALLBD = "macmallbd";

	public enum Rule {
		DEMOTE("demote"), ELEVATE("elevate"), EXCLUDE("exclude"), FACET_SORT(
				"facetSort"), REDIRECT("redirect"), RELEVANCY("relevancy");
		private String ruleName;

		Rule(String ruleName) {
			this.ruleName = ruleName;
		}

		public String getRuleName() {
			return this.ruleName;
		}
	}

	public enum Core {
		DEMOTE_RULE_CORE("demoterule"), ELEVATE_RULE_CORE("elevaterule"), EXCLUDE_RULE_CORE(
				"excluderule"), FACET_SORT_RULE_CORE("facetsortrule"), REDIRECT_RULE_CORE(
				"redirectrule"), RELEVANCY_RULE_CORE("relevancyrule");
		private String coreName;

		Core(String coreName) {
			this.coreName = coreName;
		}

		public String getCoreName() {
			return coreName;
		}
	}
}
