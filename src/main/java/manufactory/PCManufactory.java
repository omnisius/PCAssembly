package manufactory;


import actors.ChiefActor;
import actors.EngineerActor;
import actors.HucksterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import model.PC;

import java.util.concurrent.*;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

class PCManufactory {
    private static final String PC_MANUFACTORY = "PC_Manufactory";
    private static final String HUCKSTER = "Huckster";
    private static final String ENGINEER = "Engineer";
    private static final String CHIEF_ENGINEER = "Chief_Engineer";
    private static final String DETAIL_CONFIRM_MESSAGE = "Detail are compatible.";
    private static final String COMPLETE_MESSAGE = "PC is done";
    private static final String PARTS_HAS_ARRIVED = "Parts have arrived for PC ";
    private static final String HDD_MODEl = "HDD:500 Gb HDD";
    private static final String CPU_MODEl = "CPU:Intel Core i5";
    private static final String MOTHERBOARD_MODEl = "MB:SMTH13";
    private static final String RAM_MODEl = "RAM:8 Gb DDR3 L";
    private static final int TIMEOUT_HUCKSTER = 2;
    private static final int TIMEOUT_ENGINEER = 1;
    private static final int PLANNED_QUANTITY = 100000;

    static void startPCLine()  throws ExecutionException, InterruptedException {
        ActorSystem system = ActorSystem.create(PC_MANUFACTORY);
        ActorRef chiefEngineer = system.actorOf(Props.create(ChiefActor.class), CHIEF_ENGINEER);
        ActorRef engineer = system.actorOf(Props.create(EngineerActor.class), ENGINEER);
        ActorRef huckster = system.actorOf(Props.create(HucksterActor.class), HUCKSTER);
        for (int pcNumber = 0; pcNumber < PLANNED_QUANTITY; pcNumber++) {
            createPC(system, chiefEngineer, engineer, huckster, pcNumber);
        }
    }

    private static void createPC (ActorSystem system, ActorRef chiefEngineer, ActorRef engineer, ActorRef huckster, int pcNumber)
            throws InterruptedException, ExecutionException {
        PC.PCBuilder currentPC = new PC.PCBuilder(pcNumber);
        engineer.tell(currentPC, chiefEngineer);
        engineer.tell(HDD_MODEl, huckster);
        engineer.tell(RAM_MODEl, huckster);
        engineer.tell(CPU_MODEl, huckster);
        engineer.tell(MOTHERBOARD_MODEl, huckster);

        CompletableFuture<Object> hucksterFuture = ask(huckster, PARTS_HAS_ARRIVED + pcNumber, TIMEOUT_HUCKSTER).toCompletableFuture();

        CompletableFuture<Object> engineerFuture = ask(huckster, DETAIL_CONFIRM_MESSAGE, TIMEOUT_ENGINEER).toCompletableFuture();

        CompletableFuture<String> chiefEngineerFuture =
                CompletableFuture.allOf(hucksterFuture, engineerFuture)
                        .thenApply(v -> {
                            hucksterFuture.join();
                            engineerFuture.join();
                            return COMPLETE_MESSAGE;
                        });

        pipe(chiefEngineerFuture, system.dispatcher()).to(chiefEngineer);
        if (chiefEngineerFuture.isDone())
            System.out.println(chiefEngineerFuture.get());
    }
}
