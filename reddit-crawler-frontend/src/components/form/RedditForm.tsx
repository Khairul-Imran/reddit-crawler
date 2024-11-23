interface SubredditSearchProps {
    onSearch: (subreddit: string) => Promise<void>;
}

function RedditForm({ onSearch }: SubredditSearchProps) {




    return (
        <div>
            Hi this is the search form
            <form>

            </form>
        </div>
    );

}

export default RedditForm;