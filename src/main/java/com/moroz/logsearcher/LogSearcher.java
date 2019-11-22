package com.moroz.logsearcher;

import com.moroz.logsearcher.controller.Controller;
import com.moroz.logsearcher.controller.ControllerImpl;
import com.moroz.logsearcher.model.Model;
import com.moroz.logsearcher.model.ModelImpl;
import com.moroz.logsearcher.view.fxview.FXViewImpl;
import com.moroz.logsearcher.view.View;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogSearcher {

    private View view;
    private static final Logger log = LogManager.getLogger(LogSearcher.class.getName());

    public LogSearcher() {
        log.trace("call default constructor");

        Model model = new ModelImpl();
        Controller controller = new ControllerImpl();
        view = new FXViewImpl();

        view.setController(controller);
        model.setView(view);
        controller.setModel(model);
    }

    public LogSearcher(Model model, Controller controller, View view) {
        log.trace("call constructor with model: "+model+" controller: "+controller+" view: "+view);

        this.view = view;

        view.setController(controller);
        controller.setModel(model);
        model.setView(view);
    }

    public void start() {
        log.info("LogSearcher start");
        view.show();
    }
}
