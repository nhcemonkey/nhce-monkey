/*
 * BoxPane.java
 *
 * Created on 2007-6-23, 23:56:59
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common.animation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

/**
 * 
 * @author William Chen
 */
public class EffectPane extends JComponent implements ActionListener, MouseInputListener {
	
	private static final long serialVersionUID = -3600140922284367664L;
	private static final int ANIMATION_FRAMES = 20;
	private static final int ANIMATION_INTERVAL = 10;
	private int frameRadium = 100;
	private int frameIndex;
	private Timer timer;
	private Animator animator = new SimpleAnimator();
	private Point click_point;
	private Component clickedComponent;

	public EffectPane() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public Component getClickedComponent() {
		return clickedComponent;
	}

	protected void addImpl(Component comp, Object constraints, int index) {
		addHandler(comp);
		super.addImpl(comp, constraints, index);
	}

	public void remove(Component comp) {
		removeHandler(comp);
		super.remove(comp);
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (isAnimating() && click_point != null && animator != null) {
			animator.paint(this, g, click_point, frameIndex * frameRadium / ANIMATION_FRAMES, frameRadium);
		}
	}

	private boolean isAnimating() {
		return timer != null && timer.isRunning();
	}

	private void closeTimer() {
		if (isAnimating()) {
			timer.stop();
			timer = null;
			frameIndex = 0;
			if (animator != null)
				animator.destroy();
		}
	}

	public void actionPerformed(ActionEvent e) {
		frameIndex++;
		repaint();
		if (frameIndex >= ANIMATION_FRAMES)
			closeTimer();
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		Object src = e.getSource();
		if (src instanceof AbstractButton) {
			clickedComponent = (Component) src;
			Point p = getLocationOnScreen();
			click_point = e.getLocationOnScreen();
			click_point.x -= p.x;
			click_point.y -= p.y;
			closeTimer();
			if (animator != null)
				animator.init(this);

			timer = new Timer(ANIMATION_INTERVAL, this);
			timer.start();
		}
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	private void addHandler(Component comp) {
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
		if (comp instanceof Container) {
			Container container = (Container) comp;
			int count = container.getComponentCount();
			for (int i = 0; i < count; i++) {
				addHandler(container.getComponent(i));
			}
		}
	}

	private void removeHandler(Component comp) {
		comp.removeMouseListener(this);
		comp.removeMouseMotionListener(this);
		if (comp instanceof Container) {
			Container container = (Container) comp;
			int count = container.getComponentCount();
			for (int i = 0; i < count; i++) {
				removeHandler(container.getComponent(i));
			}
		}
	}

	public void setAnimator(Animator animator) {
		this.animator = animator;
	}
}
