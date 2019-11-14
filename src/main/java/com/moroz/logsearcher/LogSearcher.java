package com.moroz.logsearcher;

import com.moroz.logsearcher.controller.Controller;
import com.moroz.logsearcher.controller.ControllerImpl;
import com.moroz.logsearcher.model.Model;
import com.moroz.logsearcher.model.ModelImpl;
import com.moroz.logsearcher.view.fxview.FXViewImpl;
import com.moroz.logsearcher.view.View;

public class LogSearcher {

    private View view;

    public LogSearcher() {
        Model model = new ModelImpl();
        Controller controller = new ControllerImpl();
        view = new FXViewImpl();

        view.setController(controller);
        model.setView(view);
        controller.setModel(model);
    }

    public LogSearcher(Model model, Controller controller, View view) {
        this.view = view;

        view.setController(controller);
        controller.setModel(model);
        model.setView(view);
    }

    public void start() {
        view.show();
    }
}
