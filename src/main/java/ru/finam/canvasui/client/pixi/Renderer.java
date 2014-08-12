package ru.finam.canvasui.client.pixi;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import ru.finam.canvasui.client.JsConsole;

/**
 * Created with IntelliJ IDEA.
 * User: Superman
 * Date: 09.08.14
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
public class Renderer extends JavaScriptObject {

    protected Renderer() {}

    public static native Renderer autoDetectRenderer(int width, int height,
                                                  Canvas view, boolean transparent, boolean antialias) /*-{
        return new $wnd.PIXI.autoDetectRenderer(width, height, view, transparent, antialias);
    }-*/;

    public static native Renderer autoDetectRenderer(int width, int height) /*-{
        return new $wnd.PIXI.autoDetectRenderer(width, height);
    }-*/;

    public final native void startAnimatedRendering(Stage stage) /*-{
        var renderer = this;
        $wnd.animate = function() {
            //console.log('renderer.updateFunction = '+renderer.updateFunction);
            if (!!renderer.updateFunction) {
                renderer.updateFunction(renderer.updateFunction.displayObject);
            }
	        $wnd.requestAnimFrame($wnd.animate);
	        renderer.render(stage);
	    }
        $wnd.requestAnimFrame( $wnd.animate );
    }-*/;

    public final native String setClearBeforeRender(boolean b) /*-{
        this.clearBeforeRender = b;
    }-*/;

    public final native void setUpdateFunction(JavaScriptObject func) /*-{
        this.updateFunction = func;
    }-*/;

    public final native void render(Stage stage) /*-{
        this.render(stage);
    }-*/;

    public final native Node getView()  /*-{ return this.view;  }-*/;

    public static Renderer autoDetectRenderer(int width, int height, boolean clearBeforeRender) {
        Renderer renderer = autoDetectRenderer(width, height);
        renderer.setClearBeforeRender(clearBeforeRender);
        return renderer;
    }

    public static Renderer autoDetectRenderer(int width, int height,
                                              Canvas view, boolean transparent,
                                              boolean antialias, boolean clearBeforeRender) {
        Renderer renderer = autoDetectRenderer(width, height, view, transparent, antialias);
        renderer.setClearBeforeRender(clearBeforeRender);
        return renderer;
    }

    public static Renderer addNewRenderer(RootPanel element, int width, int height) {
        Renderer renderer = autoDetectRenderer(width, height);
        element.clear(true);
        element.getElement().appendChild(renderer.getView());
        return renderer;
    }

}