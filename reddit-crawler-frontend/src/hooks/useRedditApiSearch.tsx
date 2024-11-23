import { useCallback, useState } from "react";
import { RedditApiResponse } from "../types/RedditPostData";
import { redditDataApi } from "../services/api/redditDataApi";
import { ApiError } from "../services/api/baseApi";

interface UseRedditSearchResult {
    redditData: RedditApiResponse | undefined;
    isLoading: boolean;
    error: string | null;
    searchReddit: (subreddit: string) => Promise<void>;
}

// Custom hook
export function useRedditApiSearch(): UseRedditSearchResult {

    const [redditData, setRedditData] = useState<RedditApiResponse>();
    const [isLoading, setIsLoading] = useState(false); // Tracks loading state
    const [error, setError] = useState<string | null>(null); // Store any errors

    const searchReddit = useCallback(async (subreddit: string) => {
        console.log("useRedditApiSearch: Searching for top 20 posts in subreddit: ", subreddit);

        if (!subreddit.trim()) {
            setError("Please select a subreddit!");
            return;
        }

        setIsLoading(true);
        setError(null);

        try {

            const data = await redditDataApi.getTop20(subreddit);
            console.log("useRedditApiSearch: Received data: ", data);
            setRedditData(data);
        } catch (error) {
            console.error("Search error: ", error);

            if (error instanceof ApiError) {
                if (error.status === 400) {
                    setError(`Invalid subreddit: ${error.message}`);
                } else if (error.status >= 500) {
                    setError("Unable to fetch subreddit data. Please try again later.");
                } else {
                    setError(error.message || "An unexpected error occurred.");
                }
            } else {
                setError("Unable to fetch subreddit data. Please try again later.");
            }
        } finally {
            setIsLoading(false);
        }
    }, []);

    return {
        redditData,
        isLoading,
        error,
        searchReddit
    };
}
