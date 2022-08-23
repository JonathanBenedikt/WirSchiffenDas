import { TestBed } from '@angular/core/testing';

import { WFCommunicationService } from './wf-communication.service';

describe('WFCommunicationService', () => {
  let service: WFCommunicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WFCommunicationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
