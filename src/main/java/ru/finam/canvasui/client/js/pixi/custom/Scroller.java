package ru.finam.canvasui.client.js.pixi.custom;
import ru.finam.canvasui.client.JsConsole;
import ru.finam.canvasui.client.js.JsObject;
import ru.finam.canvasui.client.js.gsap.PropertiesSet;
import ru.finam.canvasui.client.js.gsap.TimelineLite;
import ru.finam.canvasui.client.js.pixi.*;

/**
 * Created by ikusch on 19.08.14.
 */
public class Scroller extends HasDraggableComponent {

    public static final int DEFAULT_WIDE = 3;
    public static final int DEFAULT_COLOR = 0x000000;
    public static final double DEFAULT_ALPHA = 0.6;
    public static final double DRAGGING_ALPHA = 0.9;
    public static final int MIN_LENGTH = 15;
    private static final double SCROLLER_EDGE_LENGTH = 4;
    private static final double RESIZE_ANI_DURATION = 3.8;
    private static final double SCROLL_TOGGLE_DURATION = 1;
    private static final String SCROLLER_MIDDLE_OFFSET = "scrollerMiddlePos";
    private static final String SCROLLER_TAIL_OFFSET = "scrollerTailPos";

    private DisplayObjectContainer scrollerContainer;
    private ScrollOrientation orientation;
    private Sprite scrollerMiddle;
    private Sprite scrollerForward;
    private Sprite scrollerTail;
    private double endEdge;
    private double k;
    private double fullLength;
    private double scrollPosition;
    private ScrollCallback scrollCallback;
    private Integer scrollerLength = null;
    private TimelineLite resizeTimeline;
    private PropertiesSet tScrollerLengthHolder;

    public double getScrollPosition() {
        return scrollPosition;
    }

    protected Scroller(double k,
                       double scrollPosition,
                       ScrollCallback scrollCallback, ScrollOrientation orientation, double length) {
        super();
        this.scrollPosition = scrollPosition;
        this.orientation = orientation;
        this.scrollCallback = scrollCallback;
        this.k = k;
        this.fullLength = length;
        setDragging(false);
        addGraphics();
    }

    public void doScrollCallback(double d) {
        scrollCallback.onScroll(d, this.orientation);
    }

    private static Scroller newInstance(double length, double k, ScrollCallback scrollCallback,
                                        ScrollOrientation orientation) {
        return newInstance(length, k, 0, scrollCallback, orientation);
    }

    private static Scroller newInstance(double length, double k,
                                        double scrollPosition, ScrollCallback scrollCallback,
                                        ScrollOrientation orientation) {
        return new Scroller(k, scrollPosition, scrollCallback, orientation, length);
    }

    public static Scroller newInstance(double width, double k, ScrollCallback scrollCallback,
                                                 double initAlpha, ScrollOrientation orientation) {
        Scroller scroller = newInstance(width, k, 0, scrollCallback, orientation);
        scroller.setAlpha(initAlpha);
        return scroller;
    }

    protected double draggingAlpha() {
        return DRAGGING_ALPHA;
    }

    @Override
    protected DisplayObject getDraggableComponent() {
        return this.scrollerMiddle;
    }

    @Override
    protected double getScrollerEdgeLength() {
        return SCROLLER_EDGE_LENGTH;
    }

    private void completeResizeTimeLineImmediately() {
        resizeTimeline().progress(1, true);
        dUpdateScrollerSize();
    }

    private void startAnimatedResize(double scrollerMiddleOffset, double scrollerEndOffset) {
        if (!
                ( tScrollerLengthHolder.doubleKeyValue(SCROLLER_MIDDLE_OFFSET) == scrollerMiddleOffset &&
                        tScrollerLengthHolder.doubleKeyValue(SCROLLER_TAIL_OFFSET) == scrollerEndOffset )
                )
            resizeTimeline().to(this.tScrollerLengthHolder.getJsObject(), RESIZE_ANI_DURATION,
                    new PropertiesSet().addKeyValue(SCROLLER_MIDDLE_OFFSET, scrollerMiddleOffset)
                            .addKeyValue(SCROLLER_TAIL_OFFSET, scrollerEndOffset).getJsObject(), null);
    }

    public void updateScrollPosK(double scrollPos, boolean immediatly) {
        double scrollerMiddleOffset = newScrollerMiddleCoord(scrollPos);
        double scrollerEndOffset = scrollerMiddleOffset + scrollerMiddleLength();
        if (immediatly) {
            completeResizeTimeLineImmediately();
            this.scrollPosition = scrollPos;
            updateScrollPosCoord(scrollerMiddleOffset);
        }
        else {
            startAnimatedResize(scrollerMiddleOffset, scrollerEndOffset);
        }
    }

    private void updateScrollPosCoord(double newCoord) {
        updateScrollPosCoord(newCoord, null);
    }

    private void updateScrollPosCoord(double newCoord, TouchEvent that) {
        this.tScrollerLengthHolder.addKeyValue(SCROLLER_MIDDLE_OFFSET, newCoord);
        double startEdge = SCROLLER_EDGE_LENGTH;
        if (this.orientation.equals(ScrollOrientation.HORIZONTAL)) {
            if (that != null)
                that.getPosition().setX(newCoord);
            this.scrollerForward.getPosition().setX( newCoord - startEdge );
            this.scrollerMiddle.getPosition().setX( newCoord );
            double tailPos = newCoord + this.scrollerMiddle.getWidth();
            this.scrollerTail.getPosition().setX( tailPos );
            this.tScrollerLengthHolder.addKeyValue(SCROLLER_TAIL_OFFSET, tailPos );
        }
        if (this.orientation.equals(ScrollOrientation.VERTICAL)) {
            if (that != null)
                that.getPosition().setY(newCoord);
            this.scrollerForward.getPosition().setY(newCoord - startEdge);
            this.scrollerMiddle.getPosition().setY( newCoord );
            double tailPos = newCoord + this.scrollerMiddle.getHeight();
            this.scrollerTail.getPosition().setY(tailPos);
            this.tScrollerLengthHolder.addKeyValue(SCROLLER_TAIL_OFFSET, tailPos );
        }
    }

    protected void updateDraggableCopmonents(double newOffset, TouchEvent that, double startEdge, double endEdge,
                                           ScrollOrientation orientation) {
        if (orientation.equals(this.orientation)) {
            resizeTimeline().progress(1, true);
            updateScrollPosCoord(newOffset, that);
            this.scrollPosition = (newOffset - startEdge) / (endEdge - startEdge);
            this.doScrollCallback(scrollPosition);
        }
    }

    @Override
    protected double defaultAlpha() {
        return DEFAULT_ALPHA;
    }

    protected double dragEndEdge(ScrollOrientation scrollOrientation) {
        return this.endEdge;
    }

    protected double startEdge(ScrollOrientation scrollOrientation) {
        return SCROLLER_EDGE_LENGTH;
    }

    private String getScrollerForwardTexturePath(ScrollOrientation orientation) {
        if (orientation.equals(ScrollOrientation.HORIZONTAL))
            return "img/scroller/h-scroller-left.png";
        if (orientation.equals(ScrollOrientation.VERTICAL))
            return "img/scroller/v-scroller-top.png";
        return "";
    }

    private String getScrollerMiddleTexturePath(ScrollOrientation orientation) {
        if (orientation.equals(ScrollOrientation.HORIZONTAL))
            return "img/scroller/h-scroller-center.png";
        if (orientation.equals(ScrollOrientation.VERTICAL))
            return "img/scroller/v-scroller-center.png";
        return "";
    }

    private String getScrollerTailTexturePath(ScrollOrientation orientation) {
        if (orientation.equals(ScrollOrientation.HORIZONTAL))
            return "img/scroller/h-scroller-right.png";
        if (orientation.equals(ScrollOrientation.VERTICAL))
            return "img/scroller/v-scroller-bottom.png";
        return "";
    }

    private  int scrollerMiddleLength() {
        return scrollerMiddleLength(scrollerLength);
    }

    private  int scrollerMiddleLength(int scrollerLength) {
        return (int) ( scrollerLength - 2 * SCROLLER_EDGE_LENGTH );
    }

    private int scrollerMiddleWidth(ScrollOrientation orientation, Sprite scrollerMiddle) {
        if (orientation.equals(ScrollOrientation.HORIZONTAL)) {
            return scrollerMiddleLength();
        }
        if (orientation.equals(ScrollOrientation.VERTICAL)) {
            return DEFAULT_WIDE * 2;
        }
        throw new AssertionError();
    }

    private int scrollerMiddleHeight(ScrollOrientation orientation, Sprite scrollerMiddle) {
        if (orientation.equals(ScrollOrientation.VERTICAL)) {
            return scrollerMiddleLength();
        }
        if (orientation.equals(ScrollOrientation.HORIZONTAL)) {
            return DEFAULT_WIDE * 2;
        }
        throw new AssertionError();
    }

    private Point scrollerMiddlePosition(ScrollOrientation orientation) {
        if (orientation.equals(ScrollOrientation.HORIZONTAL)) {
            return PointFactory.newInstance(SCROLLER_EDGE_LENGTH + scrollerForward.getPosition().getX(), 0);
        }
        if (orientation.equals(ScrollOrientation.VERTICAL)) {
            return PointFactory.newInstance(0, SCROLLER_EDGE_LENGTH + scrollerForward.getPosition().getY());
        }
        throw new AssertionError();
    }

    private Point scrollerTailPosition(ScrollOrientation orientation) {
        if (orientation.equals(ScrollOrientation.HORIZONTAL)) {
            return PointFactory.newInstance(( scrollerLength - SCROLLER_EDGE_LENGTH + scrollerForward.getPosition().getX() ), 0);
        }
        if (orientation.equals(ScrollOrientation.VERTICAL)) {
            return PointFactory.newInstance(0, ( scrollerLength - SCROLLER_EDGE_LENGTH + scrollerForward.getPosition().getY() ));
        }
        throw new AssertionError();
    }

    public void updateK(double k, double newScrollPos) {
        this.k = k;
        newScrollPos = newScrollPos > 1 ? newScrollPos = 1 : newScrollPos < 0 ? 0 : newScrollPos;
        updateScrollerSize(newScrollPos);
    }

    private final native JsObject onRepeat(Scroller inst) /*-{
        return function() {
            inst.@ru.finam.canvasui.client.js.pixi.custom.Scroller::dUpdateScrollerSize()();
        };
    }-*/;

    private double newScrollerMiddleCoord() {
        return newScrollerMiddleCoord(this.scrollPosition);
    }

    private double newScrollerMiddleCoord(double posK) {
        double newScrollCoord = posK * (this.fullLength - scrollerLength() ) + SCROLLER_EDGE_LENGTH;
        return newScrollCoord;
    }

    private void animatedUpdateScrollerSize(double newScrollPos) {
        double newScrollBegin = newScrollerMiddleCoord(newScrollPos);
        double newScrollerEnd = newScrollBegin + scrollerMiddleLength(scrollerLength());
        resizeTimeline().kill(null, tScrollerLengthHolder.getJsObject());
        resizeTimeline().duration(RESIZE_ANI_DURATION);
        resizeTimeline().totalDuration(RESIZE_ANI_DURATION);
        resizeTimeline().delay(0);
        resizeTimeline().to(tScrollerLengthHolder.getJsObject(), RESIZE_ANI_DURATION, new PropertiesSet().addKeyValue(SCROLLER_MIDDLE_OFFSET,
                newScrollBegin).addKeyValue(SCROLLER_TAIL_OFFSET,
                newScrollerEnd).getJsObject(), null);
    }

    private void dUpdateScrollerSize() {
        updateScrollerSizeOnly(scrollerLength((int) tScrollerLengthHolder.doubleKeyValue(SCROLLER_MIDDLE_OFFSET),
                (int) tScrollerLengthHolder.doubleKeyValue(SCROLLER_TAIL_OFFSET)));
        updateScrollPosCoord((int) tScrollerLengthHolder.doubleKeyValue(SCROLLER_MIDDLE_OFFSET));
    }

    private void updateScrollerSize() {
        updateScrollerSize(this.scrollPosition);
    }

    private void updateScrollerSize(double newScrollPos) {
        if (scrollerLength == null)
            updateScrollerSize(scrollerLength(), newScrollPos);
        else
            animatedUpdateScrollerSize(newScrollPos);
    }

    private void updateScrollerSizeOnly(int scrollerLength) {
        this.scrollerLength = scrollerLength;
        scrollerMiddle.setWidth(scrollerMiddleWidth(orientation, scrollerMiddle));
        scrollerMiddle.setHeight(scrollerMiddleHeight(orientation, scrollerMiddle));
        scrollerMiddle.setPosition(scrollerMiddlePosition(orientation));
        scrollerTail.setPosition(scrollerTailPosition(orientation));
        scrollerMiddle.setInteractive(true);
        scrollerMiddle.setButtonMode(true);
        this.endEdge = this.fullLength - scrollerLength + SCROLLER_EDGE_LENGTH;
    }

    private void updateScrollerSize(int scrollerLength, double newScrollPos) {
        updateScrollerSizeOnly(scrollerLength);
        updateScrollPosK(newScrollPos, false);
    }

    private final void addGraphics() {
        tScrollerLengthHolder = new PropertiesSet();
        tScrollerLengthHolder.addKeyValue(SCROLLER_MIDDLE_OFFSET, SCROLLER_EDGE_LENGTH);
        tScrollerLengthHolder.addKeyValue(SCROLLER_TAIL_OFFSET, this.scrollerLength() - SCROLLER_EDGE_LENGTH);

        this.scrollerContainer = DisplayObjectContainer.Factory.newInstance();
        Texture textureScrollerForward = TextureFactory.fromImage(getScrollerForwardTexturePath(orientation));
        scrollerForward = SpriteFactory.newInstance(textureScrollerForward);
        scrollerContainer.addChild(scrollerForward);
        scrollerForward.setPosition(PointFactory.newInstance(0, 0));

        Texture textureScrollerMIddle = TextureFactory.fromImage(getScrollerMiddleTexturePath(orientation));
        scrollerMiddle = SpriteFactory.newInstance(textureScrollerMIddle);
        scrollerContainer.addChild(scrollerMiddle);

        Texture textureScrollerTail = TextureFactory.fromImage(getScrollerTailTexturePath(orientation));
        scrollerTail = SpriteFactory.newInstance(textureScrollerTail);
        scrollerContainer.addChild(scrollerTail);

        updateScrollerSize();

        createDraggable(this.scrollerMiddle);
        addChild(scrollerContainer);
        scrollerContainer.setAlpha(DEFAULT_ALPHA);

        resizeTimeline().eventCallback("onUpdate", onRepeat(this), null, null);

    }

    private void onRemoveAnimationComplete() {
        this.getParent().removeChild(this.getMainComponent());
    }

    private final native JsObject onRemoveAnimationComplete(Scroller inst) /*-{
        return function() {
            inst.@ru.finam.canvasui.client.js.pixi.custom.Scroller::onRemoveAnimationComplete()();
        };
    }-*/;

    public void animatedHide() {
        animatedAlphaChange(0);
    }

    public void animatedRemove() {
        timeline().eventCallback("onComplete", onRemoveAnimationComplete(this), null, null);
        animatedHide();
    }

    public void animatedShow() {
        animatedAlphaChange(1);
    }

    public void animatedAlphaChange(double newAlpha) {
        timeline().kill(null, getMainComponent());
        timeline().to(getMainComponent(), SCROLL_TOGGLE_DURATION,
                new PropertiesSet().addKeyValue("alpha", newAlpha).getJsObject(), null);
    }

    public TimelineLite resizeTimeline() {
        if (resizeTimeline == null)
            resizeTimeline = TimelineLite.Factory.newInstance();
        return resizeTimeline;
    }

    private int scrollerLength(int middlePos, int tailPos) {
        int newLength = (int) ( tailPos - middlePos + 2 * SCROLLER_EDGE_LENGTH );
        return scrollerLength(newLength);
    }

    private int scrollerLength(int newLength) {
        return newLength > MIN_LENGTH ? newLength : MIN_LENGTH;
    }

    private int scrollerLength() {
        int newLength = (int) (this.fullLength * k);
        return scrollerLength(newLength);
    }

    public double getK() {
        return k;
    }

    @Override
    protected DisplayObject getMainDragComponent() {
        return this.scrollerContainer;
    }

}
