import { useEffect, useState } from "react";
import "./App.css";
import RedditFooter from "./components/footer/RedditFooter";
import RedditForm from "./components/form/RedditForm";
import StatusBar from "./components/form/StatusBar";
import RedditHeader from "./components/header/RedditHeader";
import { useRedditApiSearch } from "./hooks/useRedditApiSearch";
import LoadingSpinner from "./components/common/LoadingSpinner";
import ErrorMessage from "./components/common/ErrorMessage";
import RedditPostsDashboard from "./components/posts/RedditPostsDashboard";
import { RedditPost } from "./types/RedditPostData";

function App() {
    // Initialise states from localStorage or default
    const [currentSubreddit, setCurrentSubreddit] = useState<string>("");

    const [posts, setPosts] = useState<RedditPost[]>(() => {
        const savedPosts = localStorage.getItem("posts");
        return savedPosts ? JSON.parse(savedPosts) : [];
    });

    const [lastRetrievedTime, setLastRetrievedTime] = useState<Date | null>(
        () => {
            const savedTime = localStorage.getItem("lastRetrievedTime");
            return savedTime ? new Date(savedTime) : null;
        }
    );

    const { redditData, isLoading, error, searchReddit } = useRedditApiSearch();

    const handleSearch = async (subreddit: string) => {
        setCurrentSubreddit(subreddit);
        console.log("App.tsx: Searching top 20 posts for: ", subreddit);
        await searchReddit(subreddit);
    };

    // Updates the localStorage when new data is received
    useEffect(() => {
        if (redditData && redditData.length > 0) {
            setPosts(redditData);
            const currentTime = new Date();
            setLastRetrievedTime(currentTime);

            localStorage.setItem("posts", JSON.stringify(redditData));
            localStorage.setItem(
                "lastRetrievedTime",
                currentTime.toISOString()
            );
        }
    }, [redditData]); // When redditData changes

    return (
        <div className="min-h-screen bg-gray-50">
            <RedditHeader />
            <main className="container mx-auto px-4 py-8">
                <RedditForm onSearch={handleSearch} />

                {/* Conditionally render these based on the state */}
                <StatusBar lastRetrievedTime={lastRetrievedTime} />

                {isLoading && (
                    <div className="max-w-2xl mx-auto mt-8">
                        <LoadingSpinner />
                    </div>
                )}

                {error && (
                    <div className="max-w-2xl mx-auto mt-8">
                        <ErrorMessage message={error} />
                    </div>
                )}

                {/* posts was redditData */}
                {redditData && (
                    <RedditPostsDashboard
                        data={posts}
                        subreddit={currentSubreddit}
                    />
                )}
            </main>
            <RedditFooter />
        </div>
    );
}

export default App;
