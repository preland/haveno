/*
 * This file is part of Haveno.
 *
 * Haveno is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Haveno is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Haveno. If not, see <http://www.gnu.org/licenses/>.
 */

package haveno.desktop.components;

import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.LabelSkin;

import static haveno.desktop.components.TooltipUtil.showTooltipIfTruncated;

import haveno.desktop.DynamicSizeListener;
public class AutoTooltipLabel extends Label {
    
    private DynamicSizeListener ds = new DynamicSizeListener();

    public AutoTooltipLabel() {
        super();
    }

    public AutoTooltipLabel(String text) {
        super(text);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new AutoTooltipLabelSkin(this);
    }

    private class AutoTooltipLabelSkin extends LabelSkin {

        public AutoTooltipLabelSkin(Label label) {
            super(label);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(ds.scaled(x), ds.scaled(y), ds.scaled(w), ds.scaled(h));
            showTooltipIfTruncated(this, getSkinnable());
        }
    }
}
