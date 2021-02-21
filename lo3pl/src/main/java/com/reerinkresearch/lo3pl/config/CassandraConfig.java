package com.reerinkresearch.lo3pl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration {

	@Override
	public String getKeyspaceName() {
		return "gbaplkeyspace";
	}

	@Override
	public String getContactPoints() {
		return "10.108.39.25:9042";
	}

	public String[] getEntityBasePackages() {
		return new String[] { "com.reerinkresearch.lo3pl" };
	}
}