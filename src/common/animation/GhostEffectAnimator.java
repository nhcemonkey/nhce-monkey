/*
 * GhostEffectAnimator.java
 *
 * Created on June 25, 2007, 7:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package common.animation;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

/**
 *
 * @author William Chen
 */
public class GhostEffectAnimator implements Animator{
    private Image offImage;
    private Component clicked;
    /** Creates a new instance of GhostEffectAnimator */
    public GhostEffectAnimator() {
    }

    public void paint(Component c, Graphics g, Point point, int index, int total) {
        if(c==clicked)
            return;
        float ratio=(float)index/(float)total;
        int w=clicked.getWidth();
        int h=clicked.getHeight();
        int width=(int)(ratio*w)+w;
        int height=(int)(ratio*h)+h;
        Graphics2D g2d=(Graphics2D)g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (1-ratio)/2));
        Point p=c.getLocationOnScreen();
        Point cp=clicked.getLocationOnScreen();
        cp.x=cp.x-p.x+w/2;
        cp.y=cp.y-p.y+h/2;
        g2d.drawImage(offImage, cp.x-width/2, cp.y-height/2, width, height, c);
    }

    public void destroy() {
        offImage=null;
        clicked=null;
    }

    public void init(Component pane) {
        clicked=((EffectPane)pane).getClickedComponent();
        offImage=clicked.createImage(clicked.getWidth(), clicked.getHeight());
        Graphics g=offImage.getGraphics();
        clicked.paint(g);
    }
    
}
