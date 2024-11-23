import { RedditApiResponse } from "../../types/RedditPostData";
import { apiClient } from "./baseApi";

async function getTop20(searchTerm: string, signal?: AbortSignal): Promise<RedditApiResponse> {
    const response = await apiClient<RedditApiResponse>(searchTerm, {
        signal
    });

    console.log("Received response: ", response);

    return {
        data: response.data
    };
}

export const redditDataApi = {
    getTop20
};
