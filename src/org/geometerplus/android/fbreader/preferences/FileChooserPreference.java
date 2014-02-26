/*
 * Copyright (C) 2010-2014 Geometer Plus <contact@geometerplus.com>
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

package org.geometerplus.android.fbreader.preferences;

import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;

import org.geometerplus.zlibrary.core.options.ZLStringListOption;

import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;

import android.app.Activity;

import org.geometerplus.android.fbreader.preferences.PreferenceActivity;

import android.os.Parcelable;
import android.content.Intent;
import group.pals.android.lib.ui.filechooser.FileChooserActivity;
import group.pals.android.lib.ui.filechooser.io.localfile.LocalFile;

public abstract class FileChooserPreference extends Preference {
    protected int myRegCode = 0;
	
    protected abstract void setValue(String value);
    
    protected FileChooserPreference(Context context) {
		super(context);
	}
    
    protected abstract String getPath();

	@Override
	protected void onClick() {
        Intent intent = new Intent(getContext(), FileChooserActivity.class);
        intent.putExtra(FileChooserActivity._Rootpath, (Parcelable)new LocalFile(getPath()));
        intent.putExtra(FileChooserActivity._ActionBar, true);
        intent.putExtra(FileChooserActivity._FileSelectionMode, false);
        intent.putExtra(FileChooserActivity._SaveLastLocation, false);
        ((Activity)getContext()).startActivityForResult(intent, myRegCode);
	}
}
