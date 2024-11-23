import { RedditPost } from "../../types/RedditPostData";

interface PostCardProps {
    post: RedditPost;
}

function RedditPostCard({ post }: PostCardProps){

    return(
        <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow h-full flex flex-col">
            {post.photoUrl && !post.isVideo && (
                <div className="w-full">
                    <img 
                        src={post.photoUrl} 
                        alt={post.title}
                        className="w-full h-auto max-h-[300px] object-cover"
                        // className="object-cover w-full h-full"
                    />
                </div>
            )}

            <div className="p-4 flex flex-col flex-grow justify-end">
                <h3 className="font-bold text-lg mb-2 line-clamp-2">
                    {post.title}
                </h3>
                <div className="flex items-center text-sm text-gray-600 gap-4 mb-3">
                    <span>👤 {post.authorName}</span>
                    <span>⬆️ {post.upvotes.toLocaleString()}</span>
                    <span>💬 {post.totalNumberOfComments.toLocaleString()}</span>
                    <span>🔄 {post.numberOfCrossposts} crosspost{post.numberOfCrossposts !== 1 ? 's' : ''}</span>
                </div>
                
                <div className="flex justify-between items-center mt-6">
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
