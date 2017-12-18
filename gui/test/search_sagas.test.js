import configureStore from "../app/reducers/configure_store";
import { takeLatest,call,put } from 'redux-saga/effects';
import { watchSearch,fetchBibliographicPost } from '../app/sagas/index';
import {SEARCH_BIB_RECORD_ID, searchBibRecord, searchSuccess} from '../app/actions/searching';

// A bit ugly, but can't make the __mocks__ thing working
jest.mock('../app/api',()=>{
    return {
        fetchBibliographicPost(searchTerm){
            return new Promise((resolve,reject)=>{
                switch (searchTerm){
                    case '4321':
                        resolve({
                            result: [{
                                bibliographicRecordId: '4321'
                            }]
                        });
                    case 'handle error':
                        throw new Error('We had an error');
                    default:
                        resolve({result: []});
                }
            })
        },
    }
});
// This won't work
//jest.mock('../app/api');
import api from '../app/api/index';

describe('Search saga unit test',()=>{
    test('Listening for search events unit test', () => {
        const gen = watchSearch();
        expect(gen.next().value).toEqual(takeLatest(SEARCH_BIB_RECORD_ID,fetchBibliographicPost));
    });
    test('fetch bib post unit test', () => {
        // Test action
        const searchAction = searchBibRecord('4321');
        // Starting generator
        const gen = fetchBibliographicPost(searchAction);

        // Testing next step in saga is a call to api with appropriate search term
        expect(gen.next().value).toEqual(call(api.fetchBibliographicPost,searchAction.searchTerm));
        // Mock result of fetch request
        let mockResult = {
            result: [
                {
                    id: '4321'
                }
            ]
        };
        // Testing next step in saga is launching redux action searchSuccess with appropriate result
        expect(gen.next(mockResult).value).toEqual(put(searchSuccess(mockResult.result)));
    });
});


describe('Search saga integration test',()=>{
    let store;
    // Set up new Redux store for each test
    beforeEach(()=>{
        store = configureStore();
    });
    test('Successful request should end up in store',async ()=>{
        store.dispatch(searchBibRecord('4321'));
        // Will await all promises to complete, which includes saga, could fail if timers are involved
        await Promise.resolve();
        expect(store.getState().search.searchResults[0]).toEqual({bibliographicRecordId: '4321'});
    });
    test('Error in API should result in error message',async ()=>{
        store.dispatch(searchBibRecord('handle error'));
        // Will await all promises to complete, which includes saga, could fail if timers are involved
        await Promise.resolve();
        expect(store.getState().search.searchErrorMessage).toEqual('We had an error');
    })
});