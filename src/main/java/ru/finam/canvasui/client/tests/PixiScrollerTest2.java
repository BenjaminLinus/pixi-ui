package ru.finam.canvasui.client.tests;


import ru.finam.canvasui.client.js.pixi.DisplayObjectContainer;
import ru.finam.canvasui.client.js.pixi.custom.LayoutedStage;
import ru.finam.canvasui.client.js.pixi.custom.ScrollPanel;

/**
 * Created by ikusch on 14.08.14.
 */
public class PixiScrollerTest2 extends PixiScrollerTest {

    public LayoutedStage newTestStage(int width, int height, String... images) {
        LayoutedStage stage = new LayoutedStage(BG_COLOR, true);
        ScrollPanel scrollPanel = fixedSizeScrollPanel(newSampleImage(images[1]));
        stage.addChildToCenter(scrollPanel.displayObjectContainer(), width, height);
        return stage;
    }

    public String name() {
        return "Test2";
    }

    private static ScrollPanel fixedSizeScrollPanel(DisplayObjectContainer innerPanel) {
        int width = (int) innerPanel.getWidth();
        int height = (int) innerPanel.getHeight();
        ScrollPanel scrollPanel =  ScrollPanel.newInstance(innerPanel, width / 3, height / 3);
        return scrollPanel;
    }

}
