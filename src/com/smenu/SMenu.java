package com.smenu;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import processing.core.*;

/**
 * Downloaded from http://code.google.com/p/smenu/ *
 */
public class SMenu extends ArrayList {
  private static final long serialVersionUID = 1L;
  static PApplet app; static SMenu actm=null; static PFont font; static actn action=null;
  String nam; actn act=null; SMenu m,par; Rectangle r,rm; int vis=-1;
  public SMenu(PApplet ap) {this(ap,"Main",0,0);}
  public SMenu(PApplet ap,int mx, int my) {this(ap,"Popup",mx,my);}
  SMenu(PApplet ap,String s) {this(ap,s,0,0);}
  SMenu(PApplet ap,String s,int mx,int my) {
    par=actm; nam=s; 
    if (ap!=null) {
      app=ap; actm=this; font=app.createFont("Arial",12);
      app.registerDraw(this); app.registerMouseEvent(this);
      if (nam.equals("Main")) app.mouseY=-1; //to avoid appearing main menu at startup 
      rm=new Rectangle(mx,my,0,0);
    } else {
      app.pushStyle(); app.textFont(font);
      if (par.nam.equals("Main")) {
        r=new Rectangle(par.rm.x+par.rm.width,par.rm.y,textW(s),textH(s)); 
        rm=new Rectangle(r.x,par.rm.y+textH(s),0,0);
        par.rm.width+=r.width; par.rm.height=textH(s);
      } else {
        r=new Rectangle(par.rm.x,par.rm.y+par.rm.height,textW(s),textH(s));
        rm=new Rectangle(r.x+r.width,r.y,0,0);
        par.rm.height+=textH(s); par.rm.width=PApplet.max(par.rm.width,textW(s));
      }
      app.popStyle();
    }
  }
  public void SMSubM(String s) {
    actm.add(actm=new SMenu(null,s));
  }
  public void SMItem(String s,actn ac) {
    actm.add(m=new SMenu(null,s)); m.act=ac;
  }
  public void SMSep() {
    actm.add(new SMenu(null,null));
  }
  public void SMEnd() {
    actm=actm.par;
    if (actm==null) if (nam.equals("Main")) adjustH(); else adjustV();
  }
  void adjustH(){
    trimToSize();
    for (int i=0; i<size(); i++) {
      m=(SMenu)get(i);
      m.adjustV();
    }
  }
  void adjustV(){
    trimToSize();
    for (int i=0; i<size(); i++) {
      m=(SMenu)get(i);
      m.r.x=m.par.rm.x;
      m.r.width=m.par.rm.width; m.rm.x=m.r.x+m.r.width;
      m.adjustV();
    }
  }
  public void mouseEvent(MouseEvent event){
    if ((vis>=0)&&(event.getID()==MouseEvent.MOUSE_RELEASED)) {          
      if (action!=null) {action.a(); action=null;};
      vis=-1;
      if (nam.equals("Popup")) {
        app.unregisterDraw(this); app.unregisterMouseEvent(this);
      }
    } 
  }
  public void draw() {
    app.pushStyle(); app.textFont(font);app.fill(0);
    for (int i=0; i<size(); i++) {
      m=(SMenu)get(i);
      if (m.r.contains(app.mouseX,app.mouseY)) {vis=i; m.vis=999;}
    }
    if (vis>=0){
      action=null;
      app.fill(220,229,234);app.stroke(200);app.rect(rm.x,rm.y,rm.width,rm.height);
      for (int i=0; i<size(); i++) {
        m=(SMenu)get(i);
        if (vis==i){app.fill(210,225,230);app.stroke(180);app.rect(m.r.x,m.r.y,m.r.width,m.r.height);}
        if ((m.act!=null)||(m.size()>0)) app.fill(0); else app.fill(120);
        if (m.nam!=null) app.text(m.nam,m.r.x+10,m.r.y+app.textAscent());
        if ((m.par.par!=null)&&(m.size()>0))
          app.triangle(m.r.x+m.r.width-4,m.r.y+m.r.height/2-3,
                       m.r.x+m.r.width-4,m.r.y+m.r.height/2+3,
                       m.r.x+m.r.width-1,m.r.y+m.r.height/2);
        if (vis==i) m.draw();        
        if (m.r.contains(app.mouseX,app.mouseY)) action=m.act;
      } 
    }      
    app.popStyle();
  }
  int textW(String s) {
    if (s==null) return(0); else
    return((int)app.textWidth(s)+20);
  }
  int textH(String s) {
    if (s==null) return(4); else 
    return((int)(app.textAscent()+app.textDescent()));
  }    
}




