import { RedditApiResponse } from "../../types/RedditPostData";

interface SearchProps {
    data: RedditApiResponse;
}

function RedditPostsDashboard({ data }: SearchProps) {

    

    return(
        <div>Hi this is the dashboard</div>
    );
}

export default RedditPostsDashboard;