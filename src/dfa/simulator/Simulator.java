package simulator;

import java.util.ArrayList;

import java.util.Scanner;

import ast.*;
//

public class Simulator {

  // necessary for simulator
  private Statechart statechart = null;
  public static ExecutionState eState;

  public Simulator(Statechart statechart) throws Exception
  {
    try 
    {
      this.statechart = statechart;
      this.simulation();
    }
    catch (Exception e)
    {
      System.out.println("Simulation Failed! Returning from Simulator\n");
    }
  }

  

  // takes a state and returns the atomic state for it (in the process, it also executes all the entry statements for the states lying in the path)
  // also takes care of whether the state has a history tag or not
  private State get_atomic_state(State state) throws Exception
  {
    State init = state;
    try 
    {
      eState.enterState(init);                       // enters the state
      ExecuteStatement.executeStatement(init.entry); // execute the entry statement
      if(init.states.isEmpty())                      // if the state is atomic
      {
        return init;
      }
      else                                           // if the state is heirarchial
      {
        if((init.maintainsHisotry()).value)          // if the state maintains history
        {
          return this.get_atomic_state(eState.getHistoryState(init));
        }
        else                                         // if the state does not maintain history
        {
          return this.get_atomic_state(init.states.get(0));
        }
      }
    }
    catch (Exception e)                              // if the executeStatement failed
    {
      System.out.println("get_atomic_state() Failed for: " + state.getFullName() + " for the entry-statement: " + state.entry);
    }

    return init;                                    // execution never actually reaches here
  }

  // performing a transition, does the following :
  // a) gets the lower upper bound
  // b) bubbles up to the lower bound while executing all the exist statements
  // c) when it gets to the lowest upper bound, it executes the action associated with the transition
  // d) bubbles down to the destination state from the lowest upper bound
  // e) while bubbling down, it executes all the entry statements for the states in the path
  private void performTransition(Transition t, State curr) throws Exception
  {
    try
    {
      State source = t.getSource();
      State destination = t.getDestination();
      State lub = this.statechart.lub(source, destination);
      State current = curr;
      while(!current.equals(lub)) // bubbles up to lowest upper bound
      {
        ExecuteStatement.executeStatement(current.exit);
        eState.exitState(current);
        current = current.getSuperstate();
      }
      
      ExecuteStatement.executeStatement(t.action); // executes the transition action
    
      ArrayList<State> path = new ArrayList<State>();
      current = destination;
      while(!current.equals(lub)) // bubbles down to the destnation state
      {
        path.add(current);
        current = current.getSuperstate();
      }
      int i = path.size() - 1;
      while(i != 0)
      {
        eState.enterState(path.get(i));
        ExecuteStatement.executeStatement(path.get(i).entry);
        i --;
      }
    }
    catch (Exception e)
    {
      System.out.println("performTransition() failed for transition: " + t);
    }
  }

  // takes a state as the input and returns a valid transition (if available) with the input state as the source state
  Transition get_valid_transition(State curr, State fix, int i, Transition output, String event) throws Exception 
  {
    try
    {
      curr = curr.getSuperstate();
      for(Transition t : curr.transitions)
      {
        if(((BooleanConstant)EvaluateExpression.evaluate(t.guard)).value && eState.presentInConfiguration(t.getSource()) && t.trigger.equals(event))
        {
          output = t;
          i++;
        }
      }
      if(!curr.equals(this.statechart))
      {
        return this.get_valid_transition(curr, fix, i, output, event);
      }
      else
      {
        if(i == 0)
        {
          if(event.equals("null")) // if no viable transition is found
          {
            System.out.println("No Viable Transition! Halting Simulation...");
            ExecuteStatement.executeStatement(new HaltStatement());
          }
          else                    // if no viable transition is found for the current event, we re-reun the entire search with null event
          {
            return this.get_valid_transition(fix, fix, i, output, "null");
          }
        }
        else if(i > 1)
        {
          System.out.println("Non Determinism Detected At State: " + curr.name);
          System.out.println("Halting Simulation...");
          ExecuteStatement.executeStatement(new HaltStatement());
        }
        else
        {
          return output;
        }
      }
    } 
    catch (Exception e)
    {
      System.out.println("this.get_valid_transition failed to execute halt statement!\n");
    }
    return output;
  }

  // this is the main function corresponding to the simulation
  public void simulation() throws Exception 
  {
    try 
    {
      System.out.println("\n\n\n Beginning Simulation... \n\n\n");

      // populating the valueEnvironment
      eState = new ExecutionState(statechart);

      System.out.println("Initial Statechart Map: " + eState.generate_summary());
      
      Scanner input = new Scanner(System.in);                                       // to check the sequential simulation
      int counter = 0;                                                              // to label the number of transitions performed

      State curr = (State)statechart;                                               // curr represents the current state

      //Main-loop
      while(true)
      {
        curr = this.get_atomic_state(curr);                                         // gets to the state where from where the execution begins
        String event = eState.getEvent();
        input.nextLine(); // to break the flow into key-strokes

        System.out.println("+--------------------------------------------------+");
        System.out.println("After " + (counter+1) + " transition/transitions :-");
        System.out.println("From State: " + curr.getFullName());
        System.out.println("Current Event: " + event);
        Transition t = this.get_valid_transition(curr, curr, 0, null, event);
        System.out.println("Performing transition: " + t.name);
        performTransition(t, curr);
        System.out.println("Final State Map: " + eState.generate_summary());
        curr = t.getDestination();
        System.out.println("Reached State: " + curr.getFullName());
        System.out.println("+--------------------------------------------------+");
        counter ++;
      }
    }
    catch (Exception e)
    {
      System.out.println("Simulation Failed!\n"); 
    }
  }
}
//https://github.com/AmoghJohri/StatechartSimulator.git

