package manufactory;

import actors.EngineerActor;
import actors.HucksterActor;
import actors.OutspokenActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
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

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        PC.PCBuilder buildingPC = new PC.PCBuilder();
        ActorSystem system = ActorSystem.create("PCManufactory");
        ActorRef huckster = system.actorOf(Props.create(HucksterActor.class), "huckster");

        CompletableFuture<Object> hucksterFuture = ask(huckster, "Is detail good for your?", 1000).toCompletableFuture();

        System.out.println(hucksterFuture.get());

        ActorRef engineer = system.actorOf(Props.create(EngineerActor.class), "Engineer");
        Timeout slowEngineer = new Timeout(Duration.create(2, TimeUnit.SECONDS));
        CompletableFuture<Object> engineerFuture = ask(huckster, "Yes. Detail is good. We may add it to our PC", slowEngineer)
                .toCompletableFuture();

        CompletableFuture<PoisonPill> chiefEngineerFuture =
                CompletableFuture.allOf(hucksterFuture, engineerFuture)
                        .thenApply(v -> {
                            hucksterFuture.join();
                            engineerFuture.join();
                            return PoisonPill.getInstance();
                        });


        ActorRef chiefEngineer = system.actorOf(Props.create(OutspokenActor.class), "chiefEngineer");

        pipe(chiefEngineerFuture, system.dispatcher()).to(chiefEngineer);
        Thread.sleep(3000);
        System.out.println("PC is done "  );
    }
}