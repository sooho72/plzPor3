
// async function getList(replyId) {
//
//     const result = await axios.get(`/replies/${replyId}`, {params: {page,size}})
//     if(goLast){
//         console.log(result.data)
//         return result.data;
//     }
// }
async function getList(replyId) {
    const result = await axios.get(`/replies/${replyId}`);
    console.log(result.data)
    return result.data;
}


async function addReply(replyObj, postId) {

    const response = await axios.post(`/replies/${postId}`, replyObj)
    return response.data
}

async function addReReply(replyObj, parentId) {

    const response = await axios.post(`/replies/${parentId}`, replyObj);
    return response.data;

}

async function getReply(replyId) {

    const response = await axios.get(`/replies/${replyId}`);
    return response.data;

}

async function modifyReply(replyId, replyObj) {
    const response = await axios.put(`/replies/${replyId}`, replyObj);
    return response.data;
}

async function removeReply(replyId) {

    const response = await axios.delete(`/replies/${replyId}`);
    return response.data;
}