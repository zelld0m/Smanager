package com.search.manager.dao.sp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterMapper;
import org.springframework.jdbc.object.StoredProcedure;

public class LoggerStoredProcedure extends StoredProcedure {

	private static final Logger logger = LoggerFactory.getLogger(LoggerStoredProcedure.class);

	public LoggerStoredProcedure(JdbcTemplate jdbcTemplate, String storeProcedureName) {
		super(jdbcTemplate, storeProcedureName);
	}

	@Override
	public Map<String, Object> execute(Object... inParams) {
		long startTime = System.currentTimeMillis();
		try {
			return super.execute(inParams);
		} finally {
			logSPCall(startTime);
		}
	}

	@Override
	public Map<String, Object> execute(Map<String, ?> inParams) throws DataAccessException {
		long startTime = System.currentTimeMillis();
		try {
			return super.execute(inParams);
		} finally {
			logSPCall(startTime);
		}
	}

	@Override
	public Map<String, Object> execute(ParameterMapper inParamMapper) throws DataAccessException {
		long startTime = System.currentTimeMillis();
		try {
			return super.execute(inParamMapper);
		} finally {
			logSPCall(startTime);
		}
	}

	private void logSPCall(long startTime) {
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		logger.info(duration + "ms ----- " + this.getSql());
	}
}
