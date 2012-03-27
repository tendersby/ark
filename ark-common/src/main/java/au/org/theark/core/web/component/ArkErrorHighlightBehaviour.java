/*******************************************************************************
 * Copyright (c) 2011  University of Western Australia. All rights reserved.
 * 
 * This file is part of The Ark.
 * 
 * The Ark is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * The Ark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package au.org.theark.core.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

public class ArkErrorHighlightBehaviour extends Behavior {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5544350240467988658L;

	public void onComponentTag(Component c, ComponentTag tag) {
		try {
			FormComponent<?> fc = (FormComponent<?>) c;
			if (!fc.isValid()) {
				tag.put("class", "error");
			}
		}
		catch (ClassCastException cce) {
			// ignore non FormComponent Objects
		}
	}
}
