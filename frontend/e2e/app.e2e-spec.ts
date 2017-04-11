import { BusSubscriptionAppPage } from './app.po';

describe('bus-subscription-app App', () => {
  let page: BusSubscriptionAppPage;

  beforeEach(() => {
    page = new BusSubscriptionAppPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
