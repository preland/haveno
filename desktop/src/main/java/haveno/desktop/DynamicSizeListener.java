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

package haveno.desktop;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;


public class DynamicSizeListener implements ChangeListener<Number> {
        private static final double TARGET_WIDTH = 1080.0;
        private static final double BASE_FONT_SIZE = 12.0;
        private static final double SCALING_FACTOR = TARGET_WIDTH / BASE_FONT_SIZE;

        private final Scene scene;

        public DynamicSizeListener(Scene scene) {
            this.scene = scene;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            // Calculate the scaling factor based on the window width
            double scaledFont = scene.getWidth() / SCALING_FACTOR;

            // Set the scaled font size
            scene.getRoot().setStyle("-fx-font-size: " +  scaledFont + "pt;");

        }
    }

