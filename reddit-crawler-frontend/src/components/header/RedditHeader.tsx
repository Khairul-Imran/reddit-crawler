function RedditHeader(): JSX.Element {


    return(
        <header className="w-full">
            <div className="container mx-auto px-4 py-8">
                <h1 className="text-4xl font-bold text-white mb-2">Reddit Top 20</h1>
                <p className="">Check the top 20 posts from your favourite subreddit in the last 24 hours!</p>
            </div>
        </header>
    );

}

export default RedditHeader;