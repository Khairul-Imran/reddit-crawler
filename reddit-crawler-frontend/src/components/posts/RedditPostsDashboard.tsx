import { RedditPost } from "../../types/RedditPostData";
import RedditPostCard from "./RedditPostCard";
import TelegramSection from "../telegram/TelegramSection";

interface SearchProps {
    data: RedditPost[];
    subreddit: string;
}

function RedditPostsDashboard({ data, subreddit }: SearchProps) {
    return (
        <div className="max-w-7xl mx-auto mt-8">
            {data && data.length > 0 && (
                <div className="mb-6 text-center">
                    <h2 className="text-2xl font-bold text-gray-800">
                        Top 20 Posts from r/{subreddit}
                    </h2>

                    <p className="text-gray-600 mt-1 mb-6">
                        Showing {data.length} posts
                    </p>

                    {/* Buttons */}
                    <div className="flex justify-center mb-6">
                        <TelegramSection />
                    </div>
                </div>
            )}

            {/* Posts Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {data.map((post) => (
                    <RedditPostCard key={post.id} post={post} />
                ))}
            </div>
        </div>
    );
}

export default RedditPostsDashboard;
