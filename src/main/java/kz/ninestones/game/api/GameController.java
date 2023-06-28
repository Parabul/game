package kz.ninestones.game.api;

import kz.ninestones.game.core.Policy;
import kz.ninestones.game.core.State;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @GetMapping(value = "/init")
    public GameStateResponse init() {
        return new GameStateResponse(new State());
    }

    @GetMapping(value = "/state")
    public GameStateResponse state(@RequestParam(name="step") int[] steps) {
        State state = new State();

        for (int step : steps) {
            state = Policy.makeMove(state, step);
        }

        return new GameStateResponse(state);
    }
}
