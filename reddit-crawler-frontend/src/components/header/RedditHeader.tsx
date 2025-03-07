function RedditHeader(): JSX.Element {
    return (
        <header className="bg-reddit-orange w-full">
            <div className="container mx-auto px-4 py-12">
                <div className="text-center">
                    <h1 className="text-5xl font-extrabold text-white mb-4">
                        Reddit Top 20 (Done with first JenkinsPipeline!!!)
                    </h1>
                    <p className="text-lg text-white/90 max-w-2xl mx-auto leading-relaxed">
                        Check the top 20 posts from your favourite subreddit in the last 24 hours!
                    </p>
                </div>
            </div>
        </header>
    );
}

export default RedditHeader;
