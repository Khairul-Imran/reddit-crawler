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

function App() {

    // Recent searches
    // Not sure if I should store the previous search RESULTS in local storage
    // const [search, setSearch] = useState<string>();

    const { redditData, isLoading, error, searchReddit } = useRedditApiSearch();

    const handleSearch = async (subreddit: string) => {
        // setSearch(subreddit); // Removing for now
        console.log("App.tsx: Searching top 20 posts for: ", subreddit);
        await searchReddit(subreddit);
    };

    useEffect(() => {
        // localStorage.setItem("previousSearch", JSON.stringify())
    })

    return (
        <div className="min-h-screen bg-gray-50">
            <RedditHeader />
            <main className="container mx-auto px-4 py-8">

            <RedditForm onSearch={handleSearch} />
            <StatusBar />

            {/* Conditionally render these based on the state */}
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
            
            {redditData && <RedditPostsDashboard data={redditData}/>}

            </main>
            <RedditFooter />
        </div>
    );
}

export default App;
