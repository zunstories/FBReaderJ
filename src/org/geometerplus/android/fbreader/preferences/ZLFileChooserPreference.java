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

import java.util.List;
import java.util.ArrayList;

import org.geometerplus.zlibrary.core.options.ZLStringListOption;
import org.geometerplus.zlibrary.core.resources.ZLResource;

import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;

class ZLFileChooserPreference extends FileChooserPreference {
    private ZLStringListOption myOption;
    private String myPath = "";
    
	ZLFileChooserPreference(Context context, ZLResource rootResource, String resourceKey, ZLStringListOption option, int regCode) {
		super(context);
        
        myOption = option;
        myRegCode = regCode;
		
        ZLResource resource = rootResource.getResource(resourceKey);
		setTitle(resource.getValue());
        setSummary(getPath());
	}
    
    @Override
    public void setSummary(CharSequence summary){
        if(summary.length() != 0 && !myPath.equals(summary.toString())){
            super.setSummary(summary);
            setValue(summary.toString());
            myPath = summary.toString();
        }
    }
    
    protected void setValue(String value){
        final List<String> optionValues = new ArrayList<String>(myOption.getValue());
        if (optionValues.isEmpty()) {
            optionValues.add(value);
        } else {
            optionValues.set(0, value);
        }
        myOption.setValue(optionValues);
    }

    protected String getPath(){
		return myOption.getValue().isEmpty() ? "" : myOption.getValue().get(0);
    }
}
