import { useState } from "react";

interface SubredditSearchProps {
    onSearch: (subreddit: string) => Promise<void>;
}

function RedditForm({ onSearch }: SubredditSearchProps) {

    const [selectedSubreddit, setSelectedSubreddit] = useState<string>("");

    const subreddits = [
        { name: 'memes', enabled: true },
        { name: 'funny', enabled: false },
        { name: 'pics', enabled: false },
        { name: 'gaming', enabled: false },
        { name: 'aww', enabled: false },
        { name: 'todayilearned', enabled: false },
        { name: 'wholesomememes', enabled: false },
        { name: 'dankmemes', enabled: false },
        { name: 'videos', enabled: false },
        { name: 'news', enabled: false }
    ];

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (selectedSubreddit) {
            onSearch(selectedSubreddit);
        }
    };

    return (
        <div className="max-w-3xl mx-auto">
            <form onSubmit={handleSubmit} className="space-y-6">
                {/* Subreddit Options */}
                <div className="grid grid-cols-2 md:grid-cols-5 gap-3">
                    {subreddits.map((subreddit) => (
                        <button
                            key={subreddit.name}
                            type="button"
                            onClick={() => subreddit.enabled && setSelectedSubreddit(subreddit.name)}
                            className={`
                                p-3 rounded-lg text-sm font-medium
                                ${!subreddit.enabled && 'opacity-50 cursor-not-allowed'}
                                ${selectedSubreddit === subreddit.name 
                                    ? 'bg-reddit-orange text-white' 
                                    : 'bg-white text-gray-700 hover:bg-gray-50'}
                                ${subreddit.enabled 
                                    ? 'border-2 border-reddit-orange' 
                                    : 'border-2 border-gray-200'}
                            `}
                            // Disable other subreddit options
                            disabled={!subreddit.enabled}
                        >
                            r/{subreddit.name}
                        </button>
                    ))}
                </div>

                {/* Search Button */}
                <div className="flex justify-center">
                    <button
                        type="submit"
                        disabled={!selectedSubreddit}
                        className={`
                            px-6 py-2 rounded-lg font-medium
                            ${selectedSubreddit 
                                ? 'bg-reddit-orange text-white hover:bg-reddit-orange/90' 
                                : 'bg-gray-200 text-gray-500 cursor-not-allowed'}
                        `}
                    >
                        Get Top Posts
                    </button>
                </div>
            </form>
        </div>
    );
}

export default RedditForm;
