import * as actions from "../app/actions/queues";
import queuesReducer, { produceInitialState } from "../app/reducers/queues";

let queueRules = [{ queue: "q1" }, { queue: "q2" }, { queue: "q3" }];

describe("Queues action unit tests", () => {
  test("Pull queue rules action", () => {
    let desiredAction = {
      type: actions.PULL_QUEUE_RULES
    };
    expect(actions.pullQueueRules()).toEqual(desiredAction);
  });
  test("Pull queue rules success action", () => {
    let desiredAction = {
      type: actions.PULL_QUEUE_RULES_SUCCESS,
      queueRules
    };
    expect(actions.pullQueueRulesSuccess(queueRules)).toEqual(desiredAction);
  });
  test("Pull queue rules failed action", () => {
    let errorMessage = "Whoops!";
    let desiredAction = {
      type: actions.PULL_QUEUE_RULES_FAILURE,
      message: errorMessage
    };
    expect(actions.pullQueueRulesFailed(new Error(errorMessage))).toEqual(
      desiredAction
    );
  });
  test("Create queue rules action", () => {
    let queue = "q1";
    let desiredAction = {
      type: actions.CREATE_QUEUE_RULE,
      queueRule: {
        queue
      }
    };
    expect(actions.createQueueRule(queue)).toEqual(desiredAction);
  });
  test("Create queue rules successful action", () => {
    let queueRule = { queue: "qq" };
    let desiredAction = {
      type: actions.CREATE_QUEUE_RULE_SUCCESS,
      queueRule
    };
    expect(actions.createQueueRuleSuccess(queueRule)).toEqual(desiredAction);
  });
  test("Create queue rules failed action", () => {
    let errorMessage = "I cannot let you do that, Dave";
    let desiredAction = {
      type: actions.CREATE_QUEUE_RULE_FAILED,
      message: errorMessage
    };
    expect(actions.createQueueRuleFailed(new Error(errorMessage))).toEqual(
      desiredAction
    );
  });
  test("Delete queue rule action", () => {
    let queueRule = { queue: "hello" };
    let desiredAction = {
      type: actions.DELETE_QUEUE_RULE,
      queueRule
    };
    expect(actions.deleteQueueRule(queueRule)).toEqual(desiredAction);
  });
  test("Delete queue rule action succeeded", () => {
    let queueRule = { queue: "hello" };
    let desiredAction = {
      type: actions.DELETE_QUEUE_RULE_SUCCESS,
      queueRule
    };
    expect(actions.deleteQueueRuleSuccess(queueRule)).toEqual(desiredAction);
  });
  test("Delete queue rule action failed", () => {
    let errorMessage = "I am confused...";
    let desiredAction = {
      type: actions.DELETE_QUEUE_RULE_FAILED,
      message: errorMessage
    };
    expect(actions.deleteQueueRuleFailed(new Error(errorMessage))).toEqual(
      desiredAction
    );
  });
});

describe("Queues reducer unit tests", () => {
  let state;
  beforeEach(() => {
    state = produceInitialState();
  });
  test("Should return initial state", () => {
    expect(queuesReducer(undefined, {})).toEqual(state);
  });
  test("Should ignore undefined action", () => {
    expect(queuesReducer(state, undefined)).toEqual(state);
  });
  test("Reducer purity", () => {
    let action = {
      type: actions.PULL_QUEUE_RULES_FAILURE,
      message: "Soo..."
    };
    let newState = queuesReducer(state, action);
    expect(newState).not.toBe(state);
    expect(newState).not.toEqual(state);
  });
  test("Should handle pull queue rule action", () => {
    let desiredState = produceInitialState();
    desiredState.loadingQueueRules = true;
    expect(queuesReducer(state, actions.pullQueueRules())).toEqual(
      desiredState
    );
  });
  test("Should handle pull queue rule successful action", () => {
    let response = { result: queueRules, pages: 1 };
    let desiredState = produceInitialState();
    desiredState.queueRules = new Map();
    queueRules.forEach(q => desiredState.queueRules.set(q.queue, q));
    expect(
      queuesReducer(state, actions.pullQueueRulesSuccess(response))
    ).toEqual(desiredState);
  });
  test("Should handle pull queue rule failed action", () => {
    let errorMessage = "An error occured";
    let desiredState = produceInitialState();
    desiredState.queueRulesErrorMessage = errorMessage;
    expect(
      queuesReducer(
        state,
        actions.pullQueueRulesFailed(new Error(errorMessage))
      )
    ).toEqual(desiredState);
  });
  test("Should handle create queue rule action", () => {
    let queue = "qqqq";
    let desiredState = produceInitialState();
    desiredState.addQueueRulePending = true;
    expect(queuesReducer(state, actions.createQueueRule(queue))).toEqual(
      desiredState
    );
  });
  test("Should handle create queue rule successful action", () => {
    let responseQueue = { queue: "created-queue" };
    let desiredState = produceInitialState();
    let copiedQueues = queueRules.slice();
    copiedQueues.push(responseQueue);
    desiredState.queueRules = new Map();
    copiedQueues.forEach(q => desiredState.queueRules.set(q.queue, q));
    state.queueRules = new Map();
    queueRules.forEach(q => state.queueRules.set(q.queue, q));
    expect(
      queuesReducer(state, actions.createQueueRuleSuccess(responseQueue))
    ).toEqual(desiredState);
  });
  test("Should handle create queue rule failed action", () => {
    let errorMessage = "Whoopsie!";
    let desiredState = produceInitialState();
    desiredState.addQueueRuleErrorMessage = errorMessage;
    expect(
      queuesReducer(
        state,
        actions.createQueueRuleFailed(new Error(errorMessage))
      )
    ).toEqual(desiredState);
  });
  test("Should handle delete queue rule", () => {
    let deleteQueueName = "q1";
    let deleteQueue = { queue: deleteQueueName };
    // Setup
    queueRules.forEach(q => state.queueRules.set(q.queue, q));
    // Setup desired state, but without the deleted element
    let desiredState = produceInitialState();
    queueRules.forEach(q => desiredState.queueRules.set(q.queue, q));
    desiredState.deletionQueueRulePending = true;
    expect(queuesReducer(state, actions.deleteQueueRule(deleteQueue))).toEqual(
      desiredState
    );
  });
  test("Should handle delete queue rule succeeded", () => {
    let deleteQueueName = "q1";
    let deleteQueue = { queue: deleteQueueName };
    // Setup
    queueRules.forEach(q => state.queueRules.set(q.queue, q));
    // Setup desired state, but without the deleted element
    let desiredState = produceInitialState();
    queueRules.forEach(q => desiredState.queueRules.set(q.queue, q));
    desiredState.queueRules.delete(deleteQueueName);
    expect(
      queuesReducer(state, actions.deleteQueueRuleSuccess(deleteQueue))
    ).toEqual(desiredState);
  });
  test("Should handle delete of non-existing queue", () => {
    let deleteQueueName = "non-existing";
    let deleteQueue = { queue: deleteQueueName };
    // Setup
    queueRules.forEach(q => state.queueRules.set(q.queue, q));
    // Setup desired state, but without the deleted element
    let desiredState = produceInitialState();
    queueRules.forEach(q => desiredState.queueRules.set(q.queue, q));
    expect(
      queuesReducer(state, actions.deleteQueueRuleSuccess(deleteQueue))
    ).toEqual(desiredState);
  });
  test("Should handle delete of empty queue list", () => {
    let deleteQueueName = "q1";
    let deleteQueue = { queue: deleteQueueName };
    let desiredState = produceInitialState();
    expect(
      queuesReducer(state, actions.deleteQueueRuleSuccess(deleteQueue))
    ).toEqual(desiredState);
  });
  test("Should handle delete queue rule failed", () => {
    let errorMessage = "We had an error";
    let desiredState = produceInitialState();
    desiredState.deleteQueueRuleErrorMessage = errorMessage;
    expect(
      queuesReducer(
        state,
        actions.deleteQueueRuleFailed(new Error(errorMessage))
      )
    ).toEqual(desiredState);
  });
});
