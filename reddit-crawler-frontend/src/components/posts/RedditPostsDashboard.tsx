import { RedditPost } from "../../types/RedditPostData";
import RedditPostCard from "./RedditPostCard";

interface SearchProps {
    data: RedditPost[];
    subreddit: string;
}

function RedditPostsDashboard({ data, subreddit }: SearchProps) {
    return (
        <div className="max-w-7xl mx-auto mt-8">
            <div className="mb-6 text-center">
                <h2 className="text-2xl font-bold text-gray-800">
                    Top 20 Posts from r/{subreddit};
                </h2>
                {/* Not showing data length */}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {data.map((post) => (
                    <RedditPostCard key={post.id} post={post} />
                ))}
            </div>
        </div>
    );
}

export default RedditPostsDashboard;
