    import { combineReducers } from "redux";
    import { handleActions } from "redux-actions";
    import { facilitiesAction } from "./actions";

    const initialState = {
    loading: true,
    data: null,
    error: null,
    };

    const createReducer = (actionType) =>
    handleActions(
        {
        [actionType.STARTED]: (state, action) => ({
            ...state,
            loading: true,
            error: null,
        }),
        [actionType.SUCCEEDED]: (state, action) => ({
            ...state,
            loading: false,
            data: action.payload,
            error: null,
        }),
        [actionType.FAILED]: (state, action) => ({
            ...state,
            loading: false,
            error: action.payload,
        }),
        },
        initialState
    );

    const rootReducer = combineReducers({
    facilitiesList:createReducer(facilitiesAction),
    });

    export default rootReducer;
