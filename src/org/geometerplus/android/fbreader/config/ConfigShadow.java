/*
 * Copyright (C) 2007-2014 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.config;

import java.util.*;

import android.content.*;
import android.os.IBinder;
import android.os.RemoteException;

import org.geometerplus.zlibrary.core.options.Config;

public final class ConfigShadow extends Config implements ServiceConnection {
	private final Context myContext;
	private volatile ConfigInterface myInterface;
	private final List<Runnable> myDeferredActions = new LinkedList<Runnable>();

	private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			try {
				setToCache(
					intent.getStringExtra("group"),
					intent.getStringExtra("name"),
					intent.getStringExtra("value")
				);
			} catch (Exception e) {
				// ignore
			}
		}
	};

	public ConfigShadow(Context context) {
		myContext = context;
		context.bindService(
			new Intent(context, ConfigService.class),
			this,
			ConfigService.BIND_AUTO_CREATE
		);
	}

	@Override
	synchronized public boolean isInitialized() {
		return myInterface != null;
	}

	@Override
	synchronized public void runOnStart(Runnable runnable) {
		if (myInterface != null) {
			runnable.run();
		} else {
			myDeferredActions.add(runnable);
		}
	}

	@Override
	synchronized public List<String> listGroups() {
		if (myInterface == null) {
			return Collections.emptyList();
		}
		try {
			return myInterface.listGroups();
		} catch (RemoteException e) {
			return Collections.emptyList();
		}
	}

	@Override
	synchronized public List<String> listNames(String group) {
		if (myInterface == null) {
			return Collections.emptyList();
		}
		try {
			return myInterface.listNames(group);
		} catch (RemoteException e) {
			return Collections.emptyList();
		}
	}

	@Override
	synchronized public void removeGroup(String name) {
		if (myInterface != null) {
			try {
				myInterface.removeGroup(name);
			} catch (RemoteException e) {
			}
		}
	}

	@Override
	synchronized protected String getValueInternal(String group, String name) throws NotAvailableException {
		if (myInterface == null) {
			throw new NotAvailableException("Config is not initialized for " + group + ":" + name);
		}
		try {
			return myInterface.getValue(group, name);
		} catch (RemoteException e) {
			throw new NotAvailableException("RemoteException for " + group + ":" + name);
		}
	}

	@Override
	synchronized protected void setValueInternal(String group, String name, String value) {
		if (myInterface != null) {
			try {
				myInterface.setValue(group, name, value);
			} catch (RemoteException e) {
			}
		}
	}

	@Override
	synchronized protected void unsetValueInternal(String group, String name) {
		if (myInterface != null) {
			try {
				myInterface.unsetValue(group, name);
			} catch (RemoteException e) {
			}
		}
	}

	// method from ServiceConnection interface
	public synchronized void onServiceConnected(ComponentName name, IBinder service) {
		myInterface = ConfigInterface.Stub.asInterface(service);
		myContext.registerReceiver(myReceiver, new IntentFilter(SQLiteConfig.OPTION_CHANGE_EVENT_ACTION));
		for (Runnable r : myDeferredActions) {
			r.run();
		}
		myDeferredActions.clear();
	}

	// method from ServiceConnection interface
	public synchronized void onServiceDisconnected(ComponentName name) {
		myContext.unregisterReceiver(myReceiver);
	}
}
