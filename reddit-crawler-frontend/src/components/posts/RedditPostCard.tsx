import { RedditPost } from "../../types/RedditPostData";

interface PostCardProps {
    post: RedditPost;
}

function RedditPostCard({ post }: PostCardProps){

    return(
        <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
            {post.photoUrl && !post.isVideo && (
                <div className="aspect-w-16 aspect-h-9">
                    <img 
                        src={post.photoUrl} 
                        alt={post.title}
                        className="object-cover w-full h-full"
                    />
                </div>
            )}

            <div className="p-4">
                <h3 className="font-bold text-lg mb-2 line-clamp-2">
                    {post.title}
                </h3>
                <div className="flex items-center text-sm text-gray-600 gap-4">
                    <span>👤 {post.authorName}</span>
                    <span>⬆️ {post.upvotes.toLocaleString()}</span>
                    <span>💬 {post.totalNumberOfComments.toLocaleString()}</span>
                    {/* Remember to get an icon for this */}
                    <span>🔄 {post.numberOfCrossposts} crosspost{post.numberOfCrossposts !== 1 ? 's' : ''}</span>
                </div>
                <div className="mt-3 flex justify-between items-center">
                    <span className="text-sm text-gray-500">
                        {new Date(post.createdTime * 1000).toLocaleDateString()}
                    </span>
                    <a
                        href={post.postUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-500 hover:text-blue-700 text-sm"
                    >
                        View on Reddit
                    </a>
                </div>
            </div>
        </div>
    );
}

export default RedditPostCard;
