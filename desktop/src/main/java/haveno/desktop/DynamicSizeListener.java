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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;

import java.util.Timer;
import java.util.TimerTask;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

@Slf4j
public class DynamicSizeListener implements ChangeListener<Number> {
    private static final double TARGET_WIDTH = 1920.0; //1080p resolution
    private static final double BASE_FONT_SIZE = 12.0;
    private static final double TEXT_SCALING_RATIO = TARGET_WIDTH / BASE_FONT_SIZE;
    private static final long UPDATE_DELAY_MS = 100;

    public static double scalingFactor = 1.0;

    private static Scene scene = null;
    private Timer styleUpdateTimer;
    private final Object timerLock = new Object();
    public DynamicSizeListener() {
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        synchronized (timerLock) {
            if (styleUpdateTimer != null){
                styleUpdateTimer.cancel();
            }
        }
            styleUpdateTimer = new Timer();
            
            styleUpdateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    log.info("preland: updated");
                    if(scene!=null){
                        scalingFactor= scene.getWidth() / TEXT_SCALING_RATIO;
                    }
                    if(scalingFactor==0){
                        scalingFactor = 1;
                    }
                    if(scene!=null) {
                    Platform.runLater(() -> scene.getRoot().setStyle("-fx-font-size: " +  scalingFactor + "pt;"));
                    }
                    log.info("preland: scale of "+ scalingFactor + ".");
                }
            }, UPDATE_DELAY_MS);
        
    }
    public double scaled(double num) {
        if(scene!=null && num!=0) {
            double scalingRatio = TARGET_WIDTH / num;
            log.info("num: "+num+"wd: "+scene.getWidth()+"sr: "+scalingRatio);
            return scene.getWidth() / scalingRatio;
        }
        else {
            return num;
        }
        //return (num/scalingFactor);
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    public Insets ScaledInsets(double top, double right, double bottom, double left) {
        //return new Insets(top,right,bottom,left);

        return new Insets(scaled(top), scaled(right), scaled(bottom), scaled(left));
    }
}

