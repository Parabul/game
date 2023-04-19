package kz.ninestones.game.api;

import kz.ninestones.game.core.State;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @GetMapping(value = "/init")
    public GameStateResponse init() {
        return new GameStateResponse(new State());
    }
}
