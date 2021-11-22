/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.mrwho.ddd.core.ddd.support.domain;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Slawek
 * 
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")//created in domain factories, not in spring container, therefore we don't want eager creation
//@MappedSuperclass
public abstract class BaseAggregateRoot implements Serializable {
	public static enum AggregateStatus {
		ACTIVE, ARCHIVE
	}

	protected AggregateId aggregateId;

	@Version
	private Long version;

	@Enumerated(EnumType.ORDINAL)
	private AggregateStatus aggregateStatus = AggregateStatus.ACTIVE;
	
	@Transient
	@Inject
	transient protected DomainEventPublisher eventPublisher;
	
	public void markAsRemoved() {
		aggregateStatus = AggregateStatus.ARCHIVE;
	}

	public AggregateId getAggregateId() {
		return aggregateId;
	}

	public boolean isRemoved() {
		return aggregateStatus == AggregateStatus.ARCHIVE;
	}
	
	protected void domainError(String message) {
		throw new DomainOperationException(aggregateId, message);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof BaseAggregateRoot) {
			BaseAggregateRoot other = (BaseAggregateRoot) obj;
			if (other.aggregateId == null)
				return false;
			return other.aggregateId.equals(aggregateId);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {	
		if(aggregateId == null)
			return 0;
		return aggregateId.hashCode();
	}
}
