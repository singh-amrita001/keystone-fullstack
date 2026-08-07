import api from "./api";


const API_URL = "/workorders";


// GET ALL WORK ORDERS

export const getWorkOrders = (
    page = 0,
    status = "",
    sortBy = "id"
) => {


    const params = {

        page,
        size: 5,
        sortBy

    };


    // send status only when selected

    if(status && status !== "ALL") {

        params.status = status;

    }


    return api.get(
        API_URL,
        {
            params
        }
    );

};




// CREATE WORK ORDER

export const createWorkOrder = (data) => {

    return api.post(
        API_URL,
        data
    );

};




// GET BY ID

export const getWorkOrderById = (id) => {

    return api.get(
        `${API_URL}/${id}`
    );

};




// UPDATE

export const updateWorkOrder = (id,data) => {

    return api.put(
        `${API_URL}/${id}`,
        data
    );

};




// DELETE

export const deleteWorkOrder = (id) => {

    return api.delete(
        `${API_URL}/${id}`
    );

};