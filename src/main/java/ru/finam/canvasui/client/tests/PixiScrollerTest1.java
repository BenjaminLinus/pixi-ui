package ru.finam.canvasui.client.tests;


import ru.finam.canvasui.client.JsConsole;
import ru.finam.canvasui.client.js.pixi.*;
import ru.finam.canvasui.client.js.pixi.custom.CustomComponentContainer;
import ru.finam.canvasui.client.js.pixi.custom.LayoutedStage;
import ru.finam.canvasui.client.js.pixi.custom.SimplePixiPanel;
import ru.finam.canvasui.client.js.pixi.custom.scroller.ScrollPanel;

/**
 * Created with IntelliJ IDEA.
 * User: Superman
 * Date: 09.08.14
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public class PixiScrollerTest1 extends PixiScrollerTest {

    private static final int BG_COLOR = 0xFFFFFF;

    private static DisplayObjectContainer newSampleContainerWithGraphics() {
        DisplayObjectContainer sampleContainer = DisplayObjectContainer.Factory.newInstance();
        Graphics graphics = Graphics.Factory.newInstance();
        sampleContainer.addChild(graphics);
        graphics.lineStyle(1, 0x000000, 1);
        graphics.drawRect(0, 0, 30, 30);
        //JsConsole.log("sampleContainer.width = " + sampleContainer.getWidth());
        //JsConsole.log("sampleContainer.height = " + sampleContainer.getHeight());
        return sampleContainer;
    }

    public LayoutedStage newTestStage(int width, int height, String... images) {
        LayoutedStage stage = new LayoutedStage(BG_COLOR, true);
        ScrollPanel scrollPanel = fixedSizeScrollPanel1(new SimplePixiPanel(newSampleImage(images[2])));
        stage.addChildToCenter(scrollPanel.getMainComponent(), width, height);
        return stage;
    }

    public String name() {
        return "Test1";
    }

    private static ScrollPanel fixedSizeScrollPanel1(CustomComponentContainer innerPanel) {
        int width = (int) innerPanel.getWidth();
        int height = (int) innerPanel.getHeight();
        ScrollPanel scrollPanel =  ScrollPanel.newInstance(innerPanel, width / 2, height / 2);
        return scrollPanel;
    }

}