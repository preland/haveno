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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.skins.JFXButtonSkin;

import haveno.desktop.DynamicSizeListener;
import javafx.scene.Node;
import javafx.scene.control.Skin;

import static haveno.desktop.components.TooltipUtil.showTooltipIfTruncated;

public class AutoTooltipButton extends JFXButton {

    private DynamicSizeListener ds = new DynamicSizeListener();

    public AutoTooltipButton() {
        super();
    }

    public AutoTooltipButton(String text) {
        super(text.toUpperCase());
    }

    public AutoTooltipButton(String text, Node graphic) {
        super(text.toUpperCase(), graphic);
    }

    public void updateText(String text) {
        setText(text.toUpperCase());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new AutoTooltipButtonSkin(this);
    }

    private class AutoTooltipButtonSkin extends JFXButtonSkin {
        public AutoTooltipButtonSkin(JFXButton button) {
            super(button);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(ds.scaled(x), ds.scaled(y), ds.scaled(w), ds.scaled(h));
            showTooltipIfTruncated(this, getSkinnable());
        }
    }
}
