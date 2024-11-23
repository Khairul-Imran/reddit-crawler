export interface RedditPost {
    id: string,
    subReddit: string,
    title: string, // used
    authorName: string, // used
    upvoteRatio: number,
    upvotes: number, // used
    score: number,
    totalAwardsReceived: number,
    totalNumberOfComments: number, // used
    createdTime: number, // used
    photoUrl: string, // used
    postUrl: string, // used
    isVideo: boolean, // used
    postHint: string,
    domain: string,
    stickied: boolean,
    over18: boolean,
    numberOfCrossposts: number, // used
    distinguished: string
};

export interface RedditPostData {
    kind: string,  // Will be "t3" for posts
    data: RedditPost
};

export interface RedditListingData {
    after: string,
    dist: number,
    children: RedditPostData[]
}

export interface RedditApiResponse {
    data: RedditListingData
}
