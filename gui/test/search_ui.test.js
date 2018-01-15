import React from "react";
import Enzyme, { mount } from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import { Provider } from "react-redux";
import configureStore from "../app/reducers/configure_store";
import ConnectedSearchField from "../app/components/search_field";
import * as searchActions from "../app/actions/searching";
import SagaTester from 'redux-saga-tester';
import reducers from "../app/reducers";
import ourSaga from '../app/sagas';

Enzyme.configure({ adapter: new Adapter() });

let produceWrapper = store =>
    mount(
        <Provider store={store}>
          <ConnectedSearchField />
        </Provider>
    );

describe("SearchField interactions properly updates global state", () => {
  let store;
  let sagaTester;
  let wrapper;
  // Set up new Redux store for each test
  beforeEach(() => {
    store = configureStore();
    sagaTester = new SagaTester({ reducers });
    sagaTester.start(ourSaga);
    wrapper = produceWrapper(sagaTester.store);
  });
  test("Set search parameter when button is pressed",async () => {
    let getBibButton = () => wrapper.findWhere(button => button.hasClass('btn-select-search-bib-id'));
    let getRepoButton = () => wrapper.findWhere(button => button.hasClass('btn-select-search-repo-id'));
    let repoButton = getRepoButton();
    repoButton.simulate('click');
    // Reassignment needed for updated wrapper and state
    repoButton = getRepoButton();
    let bibButton = getBibButton();
    expect(repoButton.hasClass('active')).toEqual(true);
    expect(bibButton.hasClass('active')).toEqual(false);
    bibButton.simulate('click');
    repoButton = getRepoButton();
    bibButton = getBibButton();
    expect(repoButton.hasClass('active')).toEqual(false);
    expect(bibButton.hasClass('active')).toEqual(true);
  });
  test("Typing and pressing search button should set the search term in global state", () => {
    fetch.mockResponse(JSON.stringify({"result": []}));
    let getSearchInputField = () => wrapper.find('input');
    let searchInputField = getSearchInputField();
    searchInputField.simulate('change',{target: {value: '4321'}});
    let searchButton = wrapper.find('button').findWhere(button => button.hasClass('search-btn'));
    searchButton.simulate('click');
    expect(sagaTester.getState().search.searchTerm).toEqual('4321');
  });
  test("Typing and pressing Enter should set the search term in global state", () => {
    fetch.mockResponse(JSON.stringify({"result": []}));
    let getSearchInputField = () => wrapper.find('input');
    let searchInputField = getSearchInputField();
    searchInputField.simulate('change',{target: {value: '4321'}});
    searchInputField.simulate('keypress',{key: "Enter"});
    expect(sagaTester.getState().search.searchTerm).toEqual('4321');
    // Not enter should not search
    searchInputField.simulate('keypress',{key: "v"});
    expect(sagaTester.getState().search.searchTerm).toEqual('4321');
  });
  test("Pressing search button with valid id in search field should eventually update global state with result",async () => {
    // Test data
    let result1 = {
      "agencyId": 200,
      "bibliographicRecordId": "1-22",
      "work": "work:1",
      "unit": "unit:2",
      "producerVersion": "1234",
      "deleted": false,
      "indexKeys": {
        "submitter": [
          "150005"
        ],
        "term.reviewedIdentifier": [
          "9788711548288"
        ]
      }
    };
    let result2 = {
      "agencyId": 200210,
      "bibliographicRecordId": "10-31",
      "work": "work:1",
      "unit": "unit:2",
      "producerVersion": "4567",
      "deleted": false,
      "indexKeys": {
        "unit.primaryObject": [
          "150005-anmeld:120102"
        ],
        "term.reviewer": [
          "Pia Bechmann"
        ]
      }
    };
    // Mocking network calls
    fetch.mockResponseOnce(JSON.stringify({result: [result1]}));
    fetch.mockResponseOnce(JSON.stringify({result: [result2]}));
    // Simulating typing searchTerm and pressing search button
    let searchInputField = wrapper.find('input');
    let bibButton = wrapper.findWhere(button => button.hasClass('btn-select-search-bib-id'));
    bibButton.simulate('click');
    searchInputField.simulate('change',{target: {value: '4321'}});
    let searchButton = wrapper.find('button').findWhere(button => button.hasClass('search-btn'));
    searchButton.simulate('click');

    // Wait for api call to succeed and store to be updated
    await sagaTester.waitFor(searchActions.SEARCH_SUCCESS);
    expect(sagaTester.getState().search.searchResults).toEqual([result1]);

    // Test repo search with same search term
    let repoButton = wrapper.findWhere(button => button.hasClass('btn-select-search-repo-id'));
    repoButton.simulate('click');
    searchButton.simulate('click');
    // Second argument futureOnly=true since it is the second time we listen for it,
    // and therefore we want to wait for the future one
    await sagaTester.waitFor(searchActions.SEARCH_SUCCESS,true);
    expect(sagaTester.getState().search.searchResults).toEqual([result2]);
  });
});

