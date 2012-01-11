/* 
 * Copyright (c) 2008-2010, Hazel Ltd. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hazelcast.config;

import com.hazelcast.core.EntryListener;

public class EntryListenerConfig extends ListenerConfig {

	private boolean local = false;
	
	private boolean includeValue = true;
	
	public EntryListenerConfig() {
		super();
	}

	public EntryListenerConfig(String className, boolean local, boolean includeValue) {
		super(className);
		this.local = local;
		this.includeValue = includeValue;
	}

	public EntryListenerConfig(EntryListener implementation, boolean local, boolean includeValue) {
		super(implementation);
		this.local = local;
		this.includeValue = includeValue;
	}

	public EntryListener getImplementation() {
		return (EntryListener) implementation;
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public boolean isIncludeValue() {
		return includeValue;
	}

	public void setIncludeValue(boolean includeValue) {
		this.includeValue = includeValue;
	}
}