export const SEARCH_BIB_RECORD_ID = 'SEARCH_BIB_RECORD_ID';
export const SEARCH_FAILED = 'SEARCH_FAILED';
export const SEARCH_SUCCESS = 'SEARCH_SUCCESS';

export const searchSuccess = (bibPosts) => ({
    type: SEARCH_SUCCESS,
    bibPosts
});

export const searchFailed = (exception) => ({
    type: SEARCH_FAILED,
    message: exception.message
});

export const searchBibRecord = (searchTerm) => ({
    type: SEARCH_BIB_RECORD_ID,
    searchTerm
});