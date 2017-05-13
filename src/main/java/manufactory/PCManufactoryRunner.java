package manufactory;

import actors.ChiefActor;
import actors.EngineerActor;
import actors.HucksterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import model.PC;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

public class PCManufactoryRunner {

    public static final String PC_MANUFACTORY = "PCManufactory";
    public static final String HUCKSTER = "Huckster";
    public static final String ENGINEER = "Engineer";
    public static final String CHIEF_ENGINEER = "ChiefEngineer";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ActorSystem system = ActorSystem.create(PC_MANUFACTORY);
        ActorRef huckster = system.actorOf(Props.create(HucksterActor.class), HUCKSTER);

        CompletableFuture<Object> hucksterFuture = ask(huckster, "hdd", 1000).toCompletableFuture();

        System.out.println(hucksterFuture.get());

        ActorRef engineer = system.actorOf(Props.create(EngineerActor.class), ENGINEER);
        Timeout slowEngineer = new Timeout(Duration.create(2, TimeUnit.SECONDS));
        CompletableFuture<Object> engineerFuture = ask(huckster, "Yes. Detail is good. We may add it to our PC", slowEngineer)
                .toCompletableFuture();

        CompletableFuture<PC.PCBuilder> chiefEngineerFuture =
                CompletableFuture.allOf(hucksterFuture, engineerFuture)
                        .thenApply(v -> {
                            PC.PCBuilder currentPC = new PC.PCBuilder();
                            hucksterFuture.join();

                            engineerFuture.join();

                            return currentPC;
                        });


        ActorRef chiefEngineer = system.actorOf(Props.create(ChiefActor.class), CHIEF_ENGINEER);

        pipe(chiefEngineerFuture, system.dispatcher()).to(chiefEngineer);
        Thread.sleep(3000);
        if (chiefEngineerFuture.isDone())
            System.out.println("PC is done " + chiefEngineerFuture.get().build());
    }
}