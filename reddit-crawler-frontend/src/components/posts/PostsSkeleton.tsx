// To revisit and understand fully later.
function PostsSkeleton(): JSX.Element {
    // Create an array of 20 items to match our expected posts
    const skeletonPosts = Array(20).fill(null);

    return (
        <div className="max-w-7xl mx-auto mt-8">
            {/* Skeleton for title */}
            <div className="mb-6 text-center">
                <div className="h-8 w-64 bg-gray-200 rounded animate-pulse mx-auto" />
            </div>

            {/* Grid layout matching RedditPostsDashboard */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {skeletonPosts.map((_, index) => (
                    <div 
                        key={index} 
                        className="bg-white rounded-lg shadow-md overflow-hidden"
                    >
                        {/* Skeleton for image */}
                        <div className="w-full h-48 bg-gray-200 animate-pulse" />
                        
                        {/* Skeleton for content */}
                        <div className="p-4 space-y-3">
                            {/* Title skeleton */}
                            <div className="h-6 bg-gray-200 rounded animate-pulse" />
                            <div className="h-6 bg-gray-200 rounded animate-pulse w-3/4" />
                            
                            {/* Stats skeleton */}
                            <div className="flex gap-4 mt-2">
                                <div className="h-4 w-20 bg-gray-200 rounded animate-pulse" />
                                <div className="h-4 w-20 bg-gray-200 rounded animate-pulse" />
                                <div className="h-4 w-20 bg-gray-200 rounded animate-pulse" />
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default PostsSkeleton;
