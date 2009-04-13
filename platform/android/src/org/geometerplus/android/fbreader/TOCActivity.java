/*
 * Copyright (C) 2009 Geometer Plus <contact@geometerplus.com>
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

package org.geometerplus.android.fbreader;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.app.ListActivity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.tree.ZLTree;

import org.geometerplus.zlibrary.ui.android.R;

import org.geometerplus.zlibrary.text.view.impl.ZLTextWordCursor;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReader;
import org.geometerplus.fbreader.fbreader.BookTextView;

public class TOCActivity extends ListActivity {
	private TOCAdapter myAdapter;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		final FBReader fbreader = (FBReader)ZLApplication.Instance();
		final TOCTree root = fbreader.Model.TOCTree;
		myAdapter = new TOCAdapter(root);
		final ZLTextWordCursor cursor = fbreader.BookTextView.getStartCursor();
		int index = cursor.getParagraphCursor().Index;	
		if (cursor.isEndOfParagraph()) {
			++index;
		}
		TOCTree treeToSelect = null;
		// TODO: rewrite for better speed
		// TODO: process multi-model texts
		for (int i = 0; i < root.getSize(); ++i) {
			final TOCTree tree = root.getTree(i);
			final TOCTree.Reference reference = tree.getReference();
			if (reference == null) {
				continue;
			}
			if (reference.ParagraphIndex > index) {
				break;
			}
			treeToSelect = tree;
		}
		myAdapter.selectItem(treeToSelect);
		
		/*
		int selectedIndex = adapter.getSelectedIndex();
		if (selectedIndex >= 0) {
			view.setSelection(selectedIndex);
		}
		*/
	}

	private static final int PROCESS_TREE_ITEM_ID = 0;
	private static final int READ_BOOK_ITEM_ID = 1;

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final int position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
		final TOCTree tree = (TOCTree)myAdapter.getItem(position);
		switch (item.getItemId()) {
			case PROCESS_TREE_ITEM_ID:
				myAdapter.runTreeItem(tree);
				return true;
			case READ_BOOK_ITEM_ID:
				myAdapter.openBookText(tree);
				return true;
		}
		return super.onContextItemSelected(item);
	}

	private final class TOCAdapter extends ZLTreeAdapter {
		private final TOCTree myRoot;

		TOCAdapter(TOCTree root) {
			super(getListView(), root);
			myRoot = root;
		}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
			final int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
			final TOCTree tree = (TOCTree)getItem(position);
			if (tree.hasChildren()) {
				menu.setHeaderTitle(tree.getText());
				final ZLResource resource = ZLResource.resource("tocView");
				menu.add(0, PROCESS_TREE_ITEM_ID, 0, resource.getResource(isOpen(tree) ? "collapseTree" : "expandTree").getValue());
				menu.add(0, READ_BOOK_ITEM_ID, 0, resource.getResource("readText").getValue());
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final View view = (convertView != null) ? convertView :
				LayoutInflater.from(parent.getContext()).inflate(R.layout.toc_tree_item, parent, false);
			final TOCTree tree = (TOCTree)getItem(position);
			setIcon((ImageView)view.findViewById(R.id.toc_tree_item_icon), tree);
			((TextView)view.findViewById(R.id.toc_tree_item_text)).setText(tree.getText());
			return view;
		}

		void openBookText(TOCTree tree) {
			final TOCTree.Reference reference = tree.getReference();
			if (reference != null) {
				final FBReader fbreader = (FBReader)ZLApplication.Instance();
				fbreader.BookTextView.gotoParagraphSafe(reference.Model, reference.ParagraphIndex);
				finish();
			}
		}

		@Override
		protected boolean runTreeItem(ZLTree tree) {
			if (super.runTreeItem(tree)) {
				return true;
			}
			openBookText((TOCTree)tree);
			return true;
		}
	}
}
