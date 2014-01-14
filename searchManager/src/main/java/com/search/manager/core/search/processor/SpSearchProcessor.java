package com.search.manager.core.search.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Service;

import com.search.manager.core.exception.CoreSearchException;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchProcessor;
import com.search.manager.core.search.SearchResult;
import com.search.manager.dao.sp.DAOConstants;

@Service("spSearchProcessor")
public class SpSearchProcessor implements SearchProcessor {

    private StoredProcedure storedProcedure;
    private Map<String, Object> inParams;

    @SuppressWarnings("unused")
    private SpSearchProcessor() {
        // do nothing...
    }

    public SpSearchProcessor(StoredProcedure storedProcedure, Map<String, Object> inParams) {
        this.storedProcedure = storedProcedure;
        this.inParams = inParams;
    }

    @Override
    public SearchResult<?> processSearch(Search search) throws CoreSearchException {
        Map<String, Object> thisInParams = new HashMap<String, Object>(inParams);

        List<Filter> filters = search.getFilters();

        for (Filter filter : filters) {

            if (filter.getProperty().equals(DAOConstants.PARAM_MATCH_TYPE)) {
                thisInParams.put(DAOConstants.PARAM_MATCH_TYPE, filter.getValue());
                continue;
            }

            switch (filter.getOperator()) {
                case EQUAL:
                    thisInParams.put(filter.getProperty(), filter.getValue());
                    break;
                default:
                    throw new CoreSearchException("Unsupported filter operation: " + filter.getOperator());

            }
        }

        if (search.getPageNumber() > -1 && search.getMaxRowCount() > -1) {
            Integer startRow = null;
            if (search.getPageNumber() == 0 && search.getMaxRowCount() == 0) {
                startRow = 0;
            } else {
                startRow = (search.getPageNumber() - 1) * search.getMaxRowCount() + 1;
            }
            thisInParams.put(DAOConstants.PARAM_START_ROW, startRow);

            Integer endRow = null;
            if (search.getPageNumber() == 0 && search.getMaxRowCount() == 0) {
                endRow = 0;
            } else {
                endRow = search.getPageNumber() * search.getMaxRowCount();
            }
            thisInParams.put(DAOConstants.PARAM_END_ROW, endRow);
        }

        return getSearchResult(storedProcedure.execute(thisInParams));
    }

    @Override
    public String generateStrQuery(Search search) throws CoreSearchException {
        // TODO
        StringBuilder query = new StringBuilder();
        List<Filter> filters = search.getFilters();

        for (Filter filter : filters) {

            if (filter.getProperty().equals(DAOConstants.PARAM_MATCH_TYPE)) {
                inParams.put(DAOConstants.PARAM_MATCH_TYPE, filter.getValue());
                continue;
            }

            switch (filter.getOperator()) {
                case EQUAL:
                    inParams.put(filter.getProperty(), filter.getValue());
                    break;
                default:
                    throw new CoreSearchException("Unsupported filter operation: " + filter.getOperator());

            }
        }

        throw new CoreSearchException("Unimplemeted method.");
    }

    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    private SearchResult<?> getSearchResult(Map<String, Object> result) {
        ArrayList models = new ArrayList();
        int totalCount = 0;

        if (result != null) {
            models.addAll((List<?>) result.get(DAOConstants.RESULT_SET_1));
            totalCount = ((List<Integer>) result.get(DAOConstants.RESULT_SET_TOTAL)).get(0);
        }

        return new SearchResult(models, totalCount);
    }

}
