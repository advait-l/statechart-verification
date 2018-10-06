package metric;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import ast.*;
import java.util.*;

public class Metric {

  public static void scope_S(State state, Map<String, Integer> scope) {
    DeclarationList declarations = state.declarations;
    int numberOfStates = Metric.getNumberOfStates(state);
    for(Declaration declaration : declarations) {
      scope.put(declaration.getFullVName(), numberOfStates);
    }
    for(State s : state.states) {
      Metric.scope_S(s, scope);
    }
  }

  public static void scope_T(State state, Map<String, Integer> scope) {
    DeclarationList declarations = state.declarations;
    int numberOfStates = Metric.getNumberOfStates(state);
    for(Declaration declaration : declarations) {
      scope.put(declaration.getFullVName(), numberOfStates);
    }
    for(State s : state.states) {
      Metric.scope_T(s, scope);
    }
  }
  
  public static void allTransitions(State state, Set<Transition> transitions) {
 
    for(Transition transition : state.transitions) {
      transitions.add(transition);
    } 

    for(State s : state.states) {
      Metric.allTransitions(s, transitions);
    }
  }
  
  public static Set<Transition> t_IN(State state, Set<Transition> allTransitions) {
    Set<Transition> transitions = new HashSet<Transition>();
    //System.out.println("Printing transitions for state......"+state.getFullName());
    //System.out.println("Total transitions: "+allTransitions.size());
    for(Transition transition : allTransitions) {
    //System.out.println(transition.toString());
    //System.out.println("......................");
      if(transition.getDestination() == state) {
        transitions.add(transition);
      }
    }
    //System.out.println("t_IN(" + state.name + "):");
    //for(Transition transition : transitions) {
    //  System.out.print("\t" + transition.name + " ");
    //}
    //System.out.println("");
    return transitions;
  }

  public static Set<Transition> t_OUT(State state, Set<Transition> allTransitions) {
    Set<Transition> transitions = new HashSet<Transition>();
    for(Transition transition : allTransitions) {
      if(transition.getSource() == state) {
        transitions.add(transition);
      }
    }
    //System.out.println("t_OUT(" + state.name + "):");
    //for(Transition transition : transitions) {
    //  System.out.print("\t" + transition.name + " ");
    //}
    //System.out.println("");
    return transitions;
  }

  public static void Wscope(State state, Map<String, Integer> scope, Set<Transition> allTransitions) {
    DeclarationList declarations = state.declarations;
    int numberOfStates = Metric.getNumberOfStates(state);
    int numberOfTransitions = Metric.getNumberOfTransitions(state);
    
    for(State s : state.states) {
    Set<Transition> t_IN = Metric.t_IN(s, allTransitions);
    for(Declaration declaration : s.declarations) {
      scope.put(declaration.getFullVName(), Metric.getNumberOfStates(s) + Metric.getNumberOfTransitions(s) + t_IN.size());
      int m= Metric.getNumberOfStates(s) + Metric.getNumberOfTransitions(s) + t_IN.size();
     // System.out.println("I calculated this for " + declaration.getFullVName()+":"+m +" and state size is "+s.states.size());
      
    }
    if(s.states.size()>1) Metric.Wscope(s,scope,allTransitions);
    }
    //for(State s : state.states) {
    //  Metric.scope_T(s, scope);
    //}   
  }
    public static void ActualWscope(State state, Map<String, Integer> scope, Set<Transition> allTransitions) {
    DeclarationList declarations = state.declarations;
    int numberOfStates = Metric.getNumberOfStates(state);
    int numberOfTransitions = Metric.getNumberOfTransitions(state);
    
    for(State s : state.states) {
    Set<Transition> t_IN = Metric.t_IN(s, allTransitions);
    for(Declaration declaration : s.declarations) {
      scope.put(declaration.getFullVName(),randomNumberInRange(0, Metric.getNumberOfStates(s) + Metric.getNumberOfTransitions(s) + t_IN.size()));
      int m= Metric.getNumberOfStates(s) + Metric.getNumberOfTransitions(s) + t_IN.size();
     // System.out.println("I calculated this for " + declaration.getFullVName()+":"+m +" and state size is "+s.states.size());
      
    }
    if(s.states.size()>1) Metric.Wscope(s,scope,allTransitions);
    }
    //for(State s : state.states) {
    //  Metric.scope_T(s, scope);
    //}   
  }
  public static void Rscope(State state, Map<String, Integer> scope, Set<Transition> allTransitions) {
    DeclarationList declarations = state.declarations;
    int numberOfStates = Metric.getNumberOfStates(state);
    int numberOfTransitions = Metric.getNumberOfTransitions(state);
    for(State s : state.states) {
    
    Set<Transition> t_OUT = Metric.t_OUT(s, allTransitions);
    for(Declaration declaration : s.declarations) {
      scope.put(declaration.getFullVName(),  Metric.getNumberOfStates(s) + Metric.getNumberOfTransitions(s) + t_OUT.size());
      int m= Metric.getNumberOfStates(s) + Metric.getNumberOfTransitions(s) + t_OUT.size();
      //System.out.println("I calculated this for " + declaration.getFullVName()+":"+m +" and state size is "+s.states.size());
    
    }
    
    if(s.states.size()>1) Metric.Rscope(s,scope,allTransitions);
    }
    //for(State s : state.states) {
    //  Metric.scope_T(s, scope);
    //}   
  }
   public static int randomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
   public static void ActualRscope(State state, Map<String, Integer> scope, Set<Transition> allTransitions) {
    DeclarationList declarations = state.declarations;
    int numberOfStates = Metric.getNumberOfStates(state);
    int numberOfTransitions = Metric.getNumberOfTransitions(state);
    for(State s : state.states) {
    
    Set<Transition> t_OUT = Metric.t_OUT(s, allTransitions);
    for(Declaration declaration : s.declarations) {
      scope.put(declaration.getFullVName(), randomNumberInRange(0,Metric.getNumberOfStates(s) + Metric.getNumberOfTransitions(s) + t_OUT.size()));
      int m= Metric.getNumberOfStates(s) + Metric.getNumberOfTransitions(s) + t_OUT.size();
      //System.out.println("I calculated this for " + declaration.getFullVName()+":"+m +" and state size is "+s.states.size());
    
    }
    
    if(s.states.size()>1) Metric.Rscope(s,scope,allTransitions);
    }
    //for(State s : state.states) {
    //  Metric.scope_T(s, scope);
    //}   
  }
  public static int getNumberOfStates(State state) {
    
    int numberOfStates = 1;

    for(State s : state.states) {
      numberOfStates += Metric.getNumberOfStates(s);
    }

    return numberOfStates;
  }
  public static int getNumberOfCompositeStates(State state) {
    
    int numberOfCompositeStates=0;
    //if(state.getNumberOfStates()>0) 

    for(State s : state.states) {
      if(Metric.getNumberOfStates(s)>0) numberOfCompositeStates += 1;
    }

    return numberOfCompositeStates;
  }
  public static Set<State> getAllStates(State state, Set<State> totalstates) {
    //Set<State> totalstates=new HashSet<State>();
    for(State s : state.states) {
      totalstates.add(s);
      Metric.getAllStates(s,totalstates);
    }
    return totalstates;
  }
  

  public static int getNumberOfTransitions(State state) {
/*
    int numberOfTransitions = state.transitions.size();

    for(State s : state.states) {
      numberOfTransitions += Metric.getNumberOfTransitions(s);
    }
    return numberOfTransitions;
*/
    Set<Transition> transitions = new HashSet<Transition>();
    allTransitions(state, transitions);
    return transitions.size();
  }
  public static Set<Transition> getAllTransitions(State state) {
    Set<Transition> transitions = new HashSet<Transition>();
    allTransitions(state, transitions);
    return transitions;
  }

  
}