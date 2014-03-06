package com.search.manager.core.service.sp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.constant.TypeaheadDaoConstant;
import com.search.manager.core.dao.TypeaheadBrandDao;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadBrand;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.TypeaheadBrandService;
import com.search.manager.dao.sp.DAOConstants;

@Service("typeaheadBrandServiceSp")
public class TypeaheadBrandServiceSpImpl extends GenericServiceSpImpl<TypeaheadBrand> implements TypeaheadBrandService{

	@Autowired
	public TypeaheadBrandServiceSpImpl(@Qualifier("typeaheadBrandDaoSp")TypeaheadBrandDao dao) {
		super(dao);
	}

	@Override
	public TypeaheadBrand searchById(String storeId, String id)
			throws CoreServiceException {
		if (StringUtils.isBlank(storeId) || StringUtils.isBlank(id)) {
			return null;
		}
		
		Search search = new Search(TypeaheadRule.class);
		search.addFilter(new Filter(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_BRAND_ID, id));
        search.setPageNumber(1);
        search.setMaxRowCount(1);
        
        SearchResult<TypeaheadBrand> searchResult = search(search);

        if (searchResult.getTotalCount() > 0) {
            return (TypeaheadBrand) CollectionUtils.get(searchResult.getResult(), 0);
        }
        
		return null;
	}

	@Override
	public SearchResult<TypeaheadBrand> searchByRuleId(String ruleId) throws CoreServiceException {
		if (StringUtils.isBlank(ruleId)) {
			return null;
		}
		
		Search search = new Search(TypeaheadRule.class);
        search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
        search.setPageNumber(1);
        search.setMaxRowCount(1);
        
        return search(search);
	}

}
